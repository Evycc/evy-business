package com.evy.common.trace.service;

import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.web.utils.UdpUtils;
import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收集应用线程信息<br/>
 * 收集应用总线程数、线程ID、线程名、线程状态、线程堆栈信息 | 配置 : evy.trace.thread.flag={0开启|1关闭}<br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/7/5 10:17
 */
public class TraceThreadInfo {
    private static boolean THREAD_FLAG = false;
    private static final ThreadMXBean THREAD_MX_BEAN = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    private static final String THREAD_INFO_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.threadInfoInsert";
    private static final String DELETE_THREAD_INFO_BY_IP = "com.evy.common.trace.repository.mapper.TraceMapper.deleteThreadInfoByIp";

    static {
        AppContextUtils.getAsyncProp(businessProperties -> THREAD_FLAG = businessProperties.getTrace().getThread().isFlag());
        //监控死锁情况
        THREAD_MX_BEAN.setThreadContentionMonitoringEnabled(true);
        //监控死锁时间
        THREAD_MX_BEAN.setThreadCpuTimeEnabled(true);
        THREAD_MX_BEAN.setThreadAllocatedMemoryEnabled(true);
    }

    /**
     * thread dump
     * @param lockedMonitors 参考java.lang.management.ThreadMXBean#dumpAllThreads(boolean, boolean)
     * @param lockedSynchronizers 参考java.lang.management.ThreadMXBean#dumpAllThreads(boolean, boolean)
     * @return java.lang.management.ThreadInfo
     */
    public static ThreadInfo[] dumpThread(boolean lockedMonitors, boolean lockedSynchronizers) {
        return THREAD_MX_BEAN.dumpAllThreads(lockedMonitors, lockedSynchronizers);
    }

    /**
     * 导出指定线程信息
     * @param threadId 线程号
     * @return java.lang.management.ThreadInfo
     */
    public static ThreadInfo dumpThread(long threadId) {
        return THREAD_MX_BEAN.getThreadInfo(new long[]{threadId}, true, true)[0];
    }

    /**
     * 获取thread占用内存,byte
     * @param threadId 线程号
     * @return byte
     */
    public static long getThreadAllocatedBytes(long threadId) {
        return THREAD_MX_BEAN.getThreadAllocatedBytes(threadId);
    }

    /**
     * 获取所有死锁线程ID
     * @return long[]
     */
    public static long[] findDeadlockedThreads() {
        return THREAD_MX_BEAN.findDeadlockedThreads();
    }

    /**
     * thread dump
     * @return com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO
     */
    public static List<TraceThreadInfoPO> findAllThreads() {
        ThreadInfo[] threadInfos = dumpThread(true, true);
        //获取死锁状态的线程ID
//                            long[] deadlockedThreads = THREAD_MX_BEAN.findDeadlockedThreads();
        //等待死锁的线程ID
//                            long[] monitorDeadlockedThreadsId= THREAD_MX_BEAN.findMonitorDeadlockedThreads();

        List<TraceThreadInfoPO> list = new ArrayList<>(threadInfos.length);
        int tatiThreadId;
        String tatiThreadName;
        String tatiThreadStatus;
        String tatiThreadAvailByte;
        String tatiThreadStartMtime;
        String tatiThreadBlockedMtime;
        String tatiThreadBlockedName;
        int tatiThreadBlockedId;
        int tatiThreadBlockedCount;
        int tatiThreadWaitedCount;
        String tatiThreadWaitedMtime;
        int tatiThreadMaxCount = THREAD_MX_BEAN.getThreadCount();
        String tatiThreadStack;

        for (ThreadInfo threadInfo : threadInfos) {
            tatiThreadStartMtime =  String.valueOf(THREAD_MX_BEAN.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L);
            tatiThreadId = Math.toIntExact(threadInfo.getThreadId());
            tatiThreadName = threadInfo.getThreadName();
            Thread.State state = threadInfo.getThreadState();
            tatiThreadAvailByte = String.valueOf(getThreadAllocatedBytes(threadInfo.getThreadId()));
            tatiThreadStatus = state.name();
            tatiThreadStack = threadInfo.toString();

            tatiThreadBlockedCount = Math.toIntExact(threadInfo.getBlockedCount());
            //纳秒
            tatiThreadBlockedMtime = String.valueOf(threadInfo.getBlockedTime());
            tatiThreadBlockedName = threadInfo.getLockName();
            tatiThreadBlockedId = Math.toIntExact(threadInfo.getLockOwnerId());
            tatiThreadWaitedCount = Math.toIntExact(threadInfo.getWaitedCount());
            tatiThreadWaitedMtime = String.valueOf(threadInfo.getWaitedTime());

            TraceThreadInfoPO threadInfoPo = TraceThreadInfoPO.create(tatiThreadId, tatiThreadName, tatiThreadStatus, tatiThreadAvailByte, tatiThreadStartMtime, tatiThreadBlockedCount,
                    tatiThreadBlockedMtime, tatiThreadBlockedName, tatiThreadBlockedId, tatiThreadWaitedCount, tatiThreadWaitedMtime, tatiThreadMaxCount, tatiThreadStack);
            list.add(threadInfoPo);
        }

        return list;
    }

    /**
     * 通过ManagementFactory.getThreadMXBean()获取线程池及线程堆栈信息
     */
    public static void executeThreadInfo() {
        try {
            if (THREAD_FLAG) {
                List<TraceThreadInfoPO> list = findAllThreads();

                if (HealthyInfoService.isIsHealthyService()) {
                    //分段发送
                    int begin = 0;
                    int end = 0;
                    int size = list.size() / 20;
                    int listSize = list.size();
                    if (size > 1) {
                        for (int i = 1; i <= size; i++) {
                            end += 20;
                            UdpUtils.send(HealthyInfoService.getHostName(), HealthyInfoService.getPort(),
                                    HealthyInfoModel.create(TraceThreadInfoPO.class, list.subList(begin, Math.min(end, listSize))));
                            begin = end +1;
                        }
                    } else {
                        UdpUtils.send(HealthyInfoService.getHostName(), HealthyInfoService.getPort(),
                                HealthyInfoModel.create(TraceThreadInfoPO.class, list));
                    }

                } else {
                    rmThreadInfo(list);
                    addThreadInfos(list);
                }
                list = null;
            }
        } catch (Exception exception) {
            CommandLog.errorThrow("executeThreadInfo Error!", exception);
        }
    }

    /**
     * 更新服务器线程信息
     * @param list com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO
     */
    public static void addThreadInfos(List<TraceThreadInfoPO> list) {
        try {
            DBUtils.batchAny(list.stream()
                    .map(po -> DBUtils.BatchModel.create(THREAD_INFO_INSERT, po, DBUtils.BatchType.INSERT))
                    .collect(Collectors.toList()));
            list = null;
        } catch (Exception e) {
            CommandLog.errorThrow("addThreadInfos Error!", e);
        }
    }

    /**
     * 清除服务器线程信息
     * @param list  com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO
     */
    public static void rmThreadInfo(List<TraceThreadInfoPO> list) {
        list.stream().findFirst().ifPresent(traceThreadInfoPo -> DBUtils.delete(DELETE_THREAD_INFO_BY_IP, traceThreadInfoPo.getTatiAppIp()));
    }
}
