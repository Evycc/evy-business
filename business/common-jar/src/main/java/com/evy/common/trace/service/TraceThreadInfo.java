package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO;
import com.evy.common.utils.AppContextUtils;
import com.sun.management.ThreadMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收集应用线程信息<br/>
 * 收集应用总线程数、线程ID、线程名、线程状态、线程堆栈信息 | 配置 : evy.trace.thread.flag={0开启|1关闭}<br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/7/5 10:17
 */
public class TraceThreadInfo {
    private static final String THREAD_PRPO = "evy.trace.thread.flag";
    private static final ThreadMXBean THREAD_MX_BEAN = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    private static final String THREAD_INFO_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.threadInfoInsert";

    static {
        //监控死锁情况
        THREAD_MX_BEAN.setThreadContentionMonitoringEnabled(true);
        //监控死锁时间
        THREAD_MX_BEAN.setThreadCpuTimeEnabled(true);
        THREAD_MX_BEAN.setThreadAllocatedMemoryEnabled(true);
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> arr = new ArrayList<>();
        for (int i=0; i < 1000000; i++) {
            arr.add("a");
        }
        ThreadInfo[] threadInfos = THREAD_MX_BEAN.dumpAllThreads(true, true);
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("threadName:" + threadInfo.getThreadName() + " size:" + THREAD_MX_BEAN.getThreadAllocatedBytes(threadInfo.getThreadId()));
        }
        Thread.sleep(1000000);
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
     * 通过ManagementFactory.getThreadMXBean()获取线程池及线程堆栈信息
     */
    public static void executeThreadInfo() {
        try {
            Optional.ofNullable(AppContextUtils.getForEnv(THREAD_PRPO))
                    .ifPresent(flag -> {
                        if (BusinessConstant.ZERO.equals(flag)) {
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

                            DBUtils.batchAny(list.stream()
                                    .map(po -> DBUtils.BatchModel.create(THREAD_INFO_INSERT, po, DBUtils.BatchType.INSERT))
                                    .collect(Collectors.toList()));
                            list = null;
                        }
                    });
        } catch (Exception exception) {
            CommandLog.errorThrow("executeThreadInfo Error!", exception);
        }
    }
}
