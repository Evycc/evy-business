package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/6/26 0:07
 */
@Getter
@ToString
public class TraceRedisPO {
    private final String trhRedisIp;
    private final String trhRedisFlag;
    private final String trhSlaveIp;
    private final String trhClusterType;
    private final boolean trhRdbOpen;
    private final boolean trhAofOpen;
    private final boolean trhAofRdbOpen;
    private final String trhRdbFile;
    private final String trhRdbSaveType;
    private final String trhAofFile;
    private final String trhAofSaveType;
    private final String trhMemoryCount;
    private final String trhMemoryAvailableCount;
    private final String trhMemoryPeak;
    private final String trhMemoryFragmentationRatio;
    private final String trhKeyspaceRatio;
    private final String trhKeysCount;
    private final String trhLastRdbStatus;
    private final String trhLastAofStatus;
    private final String trhLastForkUsec;
    private final String trhConnTotalCount;
    private final String trhConnCurCount;
    private final String trhConnBlockCount;
    private final String trhLogPath;
    private final String trhConfigPath;
    private final String trhSentinelMonitor;
    private final String trhSentinelConfigPath;

    public TraceRedisPO(String trhRedisIp, String trhRedisFlag, String trhSlaveIp, String trhClusterType, boolean trhRdbOpen, boolean trhAofOpen, boolean trhAofRdbOpen, String trhRdbFile, String trhRdbSaveType, String trhAofFile, String trhAofSaveType, String trhMemoryCount, String trhMemoryAvailableCount, String trhMemoryPeak, String trhMemoryFragmentationRatio, String trhKeyspaceRatio, String trhKeysCount, String trhLastRdbStatus, String trhLastAofStatus, String trhLastForkUsec, String trhConnTotalCount, String trhConnCurCount, String trhConnBlockCount, String trhLogPath, String trhConfigPath, String trhSentinelMonitor, String trhSentinelConfigPath) {
        this.trhRedisIp = trhRedisIp;
        this.trhRedisFlag = trhRedisFlag;
        this.trhSlaveIp = trhSlaveIp;
        this.trhClusterType = trhClusterType;
        this.trhRdbOpen = trhRdbOpen;
        this.trhAofOpen = trhAofOpen;
        this.trhAofRdbOpen = trhAofRdbOpen;
        this.trhRdbFile = trhRdbFile;
        this.trhRdbSaveType = trhRdbSaveType;
        this.trhAofFile = trhAofFile;
        this.trhAofSaveType = trhAofSaveType;
        this.trhMemoryCount = trhMemoryCount;
        this.trhMemoryAvailableCount = trhMemoryAvailableCount;
        this.trhMemoryPeak = trhMemoryPeak;
        this.trhMemoryFragmentationRatio = trhMemoryFragmentationRatio;
        this.trhKeyspaceRatio = trhKeyspaceRatio;
        this.trhKeysCount = trhKeysCount;
        this.trhLastRdbStatus = trhLastRdbStatus;
        this.trhLastAofStatus = trhLastAofStatus;
        this.trhLastForkUsec = trhLastForkUsec;
        this.trhConnTotalCount = trhConnTotalCount;
        this.trhConnCurCount = trhConnCurCount;
        this.trhConnBlockCount = trhConnBlockCount;
        this.trhLogPath = trhLogPath;
        this.trhConfigPath = trhConfigPath;
        this.trhSentinelMonitor = trhSentinelMonitor;
        this.trhSentinelConfigPath = trhSentinelConfigPath;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     * @return 普通redis PO
     */
    public static TraceRedisPO createRedis(String trhRedisIp, String trhRedisFlag, String trhSlaveIp, String trhClusterType, boolean trhRdbOpen, boolean trhAofOpen,
                                           boolean trhAofRdbOpen, String trhRdbFile, String trhRdbSaveType, String trhAofFile, String trhAofSaveType, String trhMemoryCount,
                                           String trhMemoryAvailableCount, String trhMemoryPeak, String trhMemoryFragmentationRatio, String trhKeyspaceRatio,
                                           String trhKeysCount, String trhLastRdbStatus, String trhLastAofStatus, String trhLastForkUsec, String trhConnTotalCount,
                                           String trhConnCurCount, String trhConnBlockCount, String trhLogPath, String trhConfigPath, String trhSentinelMonitor, String trhSentinelConfigPath){
        String master = "master";
        trhRedisFlag = master.equalsIgnoreCase(trhRedisFlag) ? BusinessConstant.ZERO : BusinessConstant.ONE;

        return new TraceRedisPO(trhRedisIp, trhRedisFlag, trhSlaveIp, trhClusterType, trhRdbOpen, trhAofOpen, trhAofRdbOpen, trhRdbFile, trhRdbSaveType, trhAofFile, trhAofSaveType,
                trhMemoryCount, trhMemoryAvailableCount, trhMemoryPeak, trhMemoryFragmentationRatio, trhKeyspaceRatio, trhKeysCount, trhLastRdbStatus, trhLastAofStatus,
                trhLastForkUsec, trhConnTotalCount, trhConnCurCount, trhConnBlockCount, trhLogPath, trhConfigPath, trhSentinelMonitor, trhSentinelConfigPath);
    }
}
