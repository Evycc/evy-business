package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.HeadDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel;
import com.evy.common.utils.DateUtils;
import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.File;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 提供head dump、thread dump的功能,用于排查日常工作问题
 * @Author: EvyLiuu
 * @Date: 2021/4/4 20:30
 */
public class TraceJvmManagerUtils {
    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    private static final String HEAD_DUMP_FILE_SUFFIX = "jdk.management.heapdump.allowAnyFileSuffix";
    private static final String DUMP_FILE_SAVE_PATH_JVM_PARAM = "HeapDumpPath";
    private static final String DUMP_FILE_SAVE_PATH = "/applog/current/dump/";
    private static final String FILE_SUFFIX_NAME = ".hprof";
    private static final String DUMP_FILE_PATH = System.getProperty(DUMP_FILE_SAVE_PATH_JVM_PARAM, DUMP_FILE_SAVE_PATH);
    /**
     * 通过该类进行head dump
     */
    private static HotSpotDiagnosticMXBean HOT_SPOT_DIAGNOSTIC_MX_BEAN;

    static {
        try {
            //初始化方式参照sun.tools.jconsole.ProxyClient.getHotSpotDiagnosticMXBean
            HOT_SPOT_DIAGNOSTIC_MX_BEAN = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
            //初始化dump目录
            File file = new File(DUMP_FILE_PATH);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    CommandLog.error("#ERROR# 初始化Dump目录 {} 失败,将会影响head dump的导出", file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            CommandLog.errorThrow("#ERROR# 初始化HotSpotDiagnosticMXBean,将会影响head dump的在线导出", e);
        }
    }

    /**
     * 在服务器固定路径导出head dump文件<br/>
     * 通过jvm参数指定生成目录-XX:HeapDumpPath<br/>
     * 默认生成到目录/applog/current/dump/
     * @return com.evy.common.trace.infrastructure.tunnel.model.HeadDumpInfoModel
     */
    public static HeadDumpInfoModel heapDump() {
        //生成dump文件到指定目录
        HeadDumpInfoModel model = new HeadDumpInfoModel();
        String dumpFileName = "dump" + DateUtils.now(DateUtils.YYYYMMDDHHMMSS) + FILE_SUFFIX_NAME;
        String dumpFile = DUMP_FILE_PATH + dumpFileName;
        File file = new File(dumpFile);
        model.setDumpFilePath(dumpFile);
        model.setDumpResult(BusinessConstant.SUCESS);
        if (!file.exists()) {
            try {
                HOT_SPOT_DIAGNOSTIC_MX_BEAN.dumpHeap(dumpFile, true);
            } catch (IOException e) {
                model.setDumpResult(BusinessConstant.FAILED);
                model.setDumpResultErrorText(e.getMessage());
                CommandLog.errorThrow("#ERROR# 导出head dump文件异常", e);
            }
        }

        return model;
    }

    /**
     * 实时查找thread信息
     * @param threadId 线程号
     * @return com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel
     */
    public static ThreadDumpInfoModel threadDump(long threadId) {
        ThreadDumpInfoModel model = new ThreadDumpInfoModel();
        try {
            ThreadInfo threadInfo = TraceThreadInfo.dumpThread(threadId);
            if (Objects.isNull(threadInfo)) {
                model.setDumpResult(BusinessConstant.ONE_NUM);
                model.setDumpResultErrorText("不存在的线程");
            } else {
                model.setDumpResult(BusinessConstant.ZERO_NUM);
                model.setThreadAvailByte(String.valueOf(TraceThreadInfo.getThreadAllocatedBytes(threadId)));
                model.setThreadName(threadInfo.getThreadName());
                //堆栈跟踪
                model.setThreadStack(threadInfo.toString());
                model.setThreadState(threadInfo.getThreadState().name());
                if (Thread.State.BLOCKED.equals(threadInfo.getThreadState())) {
                    //总阻止数
                    model.setThreadBlockedCount(Math.toIntExact(threadInfo.getBlockedCount()));
                    //持有锁时间
                    model.setThreadBlockedTimeMs(String.valueOf(threadInfo.getBlockedTime()));
                    //持有锁名称
                    model.setThreadBlockedName(threadInfo.getLockName());
                    //等待当前线程释放锁的线程ID
                    model.setThreadBlockedId(Math.toIntExact(threadInfo.getLockOwnerId()));
                } else if (Thread.State.WAITING.equals(threadInfo.getThreadState()) || Thread.State.TIMED_WAITING.equals(threadInfo.getThreadState())) {
                    //找出当前线程等待锁释放的线程ID
                    model.setThreadWaitedTimeMs(String.valueOf(threadInfo.getWaitedTime()));
                    MonitorInfo[] monitorInfos = threadInfo.getLockedMonitors();

                    if (Objects.nonNull(monitorInfos)) {
                        String monitors = Arrays.toString(monitorInfos);
                        model.setLockedMonitors(monitors);
                        StringBuilder stringBuilder = new StringBuilder();
                        ThreadInfo[] threadInfos = TraceThreadInfo.dumpThread(true, true);

                        for (ThreadInfo info : threadInfos) {
                            LockInfo lockInfo = info.getLockInfo();
                            if (Objects.nonNull(lockInfo) && monitors.contains(lockInfo.toString())) {
                                //存在等待threadId的线程
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(BusinessConstant.LINE);
                                }
                                stringBuilder.append(info.getThreadId());
                            }
                        }
                        model.setWaitFromThreadIds(stringBuilder.toString());
                    }
                }
            }
        } catch (Exception exception) {
            model.setDumpResult(BusinessConstant.ONE_NUM);
            model.setDumpResultErrorText(exception.getMessage());
            CommandLog.errorThrow("#ERROR# 查询threadID:" + threadId +"异常", exception);
        }

        return model;
    }

    /**
     * 获取所有死锁线程信息
     * @return com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel
     */
    public static List<ThreadDumpInfoModel> findDeadThreads() {
        long[] deadThreads = TraceThreadInfo.findDeadlockedThreads();
        boolean hasDeadThread = Objects.nonNull(deadThreads);
        List<ThreadDumpInfoModel> result = new ArrayList<>(hasDeadThread ? deadThreads.length : 0);

        if (hasDeadThread) {
            for (long deadThread : deadThreads) {
                result.add(threadDump(deadThread));
            }
        }

        return result;
    }
}