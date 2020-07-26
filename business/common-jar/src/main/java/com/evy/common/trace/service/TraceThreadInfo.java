package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.db.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoListPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
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
    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
    private static final String THREAD_INFO_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.threadInfoInsert";

    static {
        //监控死锁情况
        THREAD_MX_BEAN.setThreadContentionMonitoringEnabled(true);
        //监控死锁时间
        THREAD_MX_BEAN.setThreadCpuTimeEnabled(true);
    }

    /**
     * 通过ManagementFactory.getThreadMXBean()获取线程池及线程堆栈信息
     */
    public static void executeThreadInfo() {
        try {
            Optional.ofNullable(AppContextUtils.getForEnv(THREAD_PRPO))
                    .ifPresent(flag -> {
                        if (BusinessConstant.ZERO.equals(flag)) {
                            ThreadInfo[] threadInfos = THREAD_MX_BEAN.dumpAllThreads(true, true);
                            //获取死锁状态的线程ID
//                            long[] deadlockedThreads = THREAD_MX_BEAN.findDeadlockedThreads();
                            //等待死锁的线程ID
//                            long[] monitorDeadlockedThreadsId= THREAD_MX_BEAN.findMonitorDeadlockedThreads();

                            List<TraceThreadInfoPO> list = new ArrayList<>(threadInfos.length);
                            int tatiThreadId;
                            String tatiThreadName;
                            String tatiThreadStatus;
                            String tatiThreadStartMtime = String.valueOf(THREAD_MX_BEAN.getCurrentThreadCpuTime());
                            String tatiThreadBlockedMtime;
                            String tatiThreadBlockedName;
                            int tatiThreadBlockedId;
                            int tatiThreadBlockedCount;
                            int tatiThreadWaitedCount;
                            String tatiThreadWaitedMtime;
                            int tatiThreadMaxCount = THREAD_MX_BEAN.getThreadCount();
                            String tatiThreadStack;

                            for (ThreadInfo threadInfo : threadInfos) {
                                tatiThreadId = CommandUtils.longParsetoInt(threadInfo.getThreadId());
                                tatiThreadName = threadInfo.getThreadName();
                                Thread.State state = threadInfo.getThreadState();
                                tatiThreadStatus = state.name();
                                tatiThreadStack = threadInfo.toString();

                                tatiThreadBlockedCount = CommandUtils.longParsetoInt(threadInfo.getBlockedCount());
                                //纳秒
                                tatiThreadBlockedMtime = String.valueOf(threadInfo.getBlockedTime());
                                tatiThreadBlockedName = threadInfo.getLockName();
                                tatiThreadBlockedId = CommandUtils.longParsetoInt(threadInfo.getLockOwnerId());
                                tatiThreadWaitedCount = CommandUtils.longParsetoInt(threadInfo.getWaitedCount());
                                tatiThreadWaitedMtime = String.valueOf(threadInfo.getWaitedTime());

                                TraceThreadInfoPO threadInfoPo = TraceThreadInfoPO.create(tatiThreadId, tatiThreadName, tatiThreadStatus, tatiThreadStartMtime, tatiThreadBlockedCount,
                                        tatiThreadBlockedMtime, tatiThreadBlockedName, tatiThreadBlockedId, tatiThreadWaitedCount, tatiThreadWaitedMtime, tatiThreadMaxCount, tatiThreadStack);
                                list.add(threadInfoPo);
                            }

                            DBUtils.batchAny(list.stream()
                                    .map(po -> DBUtils.BatchModel.create(THREAD_INFO_INSERT, po, DBUtils.BatchType.INSERT))
                                    .collect(Collectors.toList()));
                        }
                    });
        } catch (Exception exception) {
            CommandLog.errorThrow("executeThreadInfo Error!", exception);
        }
    }
}
