package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;

/**
 * @Author: EvyLiuu
 * @Date: 2020/6/26 0:07
 */
public class TraceRedisPO {
    private final String trhAppIp;
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

    public TraceRedisPO(String trhAppIp, String trhRedisIp, String trhRedisFlag, String trhSlaveIp, String trhClusterType, boolean trhRdbOpen, boolean trhAofOpen, boolean trhAofRdbOpen, String trhRdbFile, String trhRdbSaveType, String trhAofFile, String trhAofSaveType, String trhMemoryCount, String trhMemoryAvailableCount, String trhMemoryPeak, String trhMemoryFragmentationRatio, String trhKeyspaceRatio, String trhKeysCount, String trhLastRdbStatus, String trhLastAofStatus, String trhLastForkUsec, String trhConnTotalCount, String trhConnCurCount, String trhConnBlockCount, String trhLogPath, String trhConfigPath, String trhSentinelMonitor, String trhSentinelConfigPath) {
        this.trhAppIp = trhAppIp;
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

        return new TraceRedisPO(BusinessConstant.VM_HOST, trhRedisIp, trhRedisFlag, trhSlaveIp, trhClusterType, trhRdbOpen, trhAofOpen, trhAofRdbOpen, trhRdbFile, trhRdbSaveType, trhAofFile, trhAofSaveType,
                trhMemoryCount, trhMemoryAvailableCount, trhMemoryPeak, trhMemoryFragmentationRatio, trhKeyspaceRatio, trhKeysCount, trhLastRdbStatus, trhLastAofStatus,
                trhLastForkUsec, trhConnTotalCount, trhConnCurCount, trhConnBlockCount, trhLogPath, trhConfigPath, trhSentinelMonitor, trhSentinelConfigPath);
    }

    @Override
    public String toString() {
        return "TraceRedisPO{" +
                "trhAppIp='" + trhAppIp + '\'' +
                ", trhRedisIp='" + trhRedisIp + '\'' +
                ", trhRedisFlag='" + trhRedisFlag + '\'' +
                ", trhSlaveIp='" + trhSlaveIp + '\'' +
                ", trhClusterType='" + trhClusterType + '\'' +
                ", trhRdbOpen=" + trhRdbOpen +
                ", trhAofOpen=" + trhAofOpen +
                ", trhAofRdbOpen=" + trhAofRdbOpen +
                ", trhRdbFile='" + trhRdbFile + '\'' +
                ", trhRdbSaveType='" + trhRdbSaveType + '\'' +
                ", trhAofFile='" + trhAofFile + '\'' +
                ", trhAofSaveType='" + trhAofSaveType + '\'' +
                ", trhMemoryCount='" + trhMemoryCount + '\'' +
                ", trhMemoryAvailableCount='" + trhMemoryAvailableCount + '\'' +
                ", trhMemoryPeak='" + trhMemoryPeak + '\'' +
                ", trhMemoryFragmentationRatio='" + trhMemoryFragmentationRatio + '\'' +
                ", trhKeyspaceRatio='" + trhKeyspaceRatio + '\'' +
                ", trhKeysCount='" + trhKeysCount + '\'' +
                ", trhLastRdbStatus='" + trhLastRdbStatus + '\'' +
                ", trhLastAofStatus='" + trhLastAofStatus + '\'' +
                ", trhLastForkUsec='" + trhLastForkUsec + '\'' +
                ", trhConnTotalCount='" + trhConnTotalCount + '\'' +
                ", trhConnCurCount='" + trhConnCurCount + '\'' +
                ", trhConnBlockCount='" + trhConnBlockCount + '\'' +
                ", trhLogPath='" + trhLogPath + '\'' +
                ", trhConfigPath='" + trhConfigPath + '\'' +
                ", trhSentinelMonitor='" + trhSentinelMonitor + '\'' +
                ", trhSentinelConfigPath='" + trhSentinelConfigPath + '\'' +
                '}';
    }

    public String getTrhAppIp() {
        return trhAppIp;
    }

    public String getTrhRedisIp() {
        return trhRedisIp;
    }

    public String getTrhRedisFlag() {
        return trhRedisFlag;
    }

    public String getTrhSlaveIp() {
        return trhSlaveIp;
    }

    public String getTrhClusterType() {
        return trhClusterType;
    }

    public boolean isTrhRdbOpen() {
        return trhRdbOpen;
    }

    public boolean isTrhAofOpen() {
        return trhAofOpen;
    }

    public boolean isTrhAofRdbOpen() {
        return trhAofRdbOpen;
    }

    public String getTrhRdbFile() {
        return trhRdbFile;
    }

    public String getTrhRdbSaveType() {
        return trhRdbSaveType;
    }

    public String getTrhAofFile() {
        return trhAofFile;
    }

    public String getTrhAofSaveType() {
        return trhAofSaveType;
    }

    public String getTrhMemoryCount() {
        return trhMemoryCount;
    }

    public String getTrhMemoryAvailableCount() {
        return trhMemoryAvailableCount;
    }

    public String getTrhMemoryPeak() {
        return trhMemoryPeak;
    }

    public String getTrhMemoryFragmentationRatio() {
        return trhMemoryFragmentationRatio;
    }

    public String getTrhKeyspaceRatio() {
        return trhKeyspaceRatio;
    }

    public String getTrhKeysCount() {
        return trhKeysCount;
    }

    public String getTrhLastRdbStatus() {
        return trhLastRdbStatus;
    }

    public String getTrhLastAofStatus() {
        return trhLastAofStatus;
    }

    public String getTrhLastForkUsec() {
        return trhLastForkUsec;
    }

    public String getTrhConnTotalCount() {
        return trhConnTotalCount;
    }

    public String getTrhConnCurCount() {
        return trhConnCurCount;
    }

    public String getTrhConnBlockCount() {
        return trhConnBlockCount;
    }

    public String getTrhLogPath() {
        return trhLogPath;
    }

    public String getTrhConfigPath() {
        return trhConfigPath;
    }

    public String getTrhSentinelMonitor() {
        return trhSentinelMonitor;
    }

    public String getTrhSentinelConfigPath() {
        return trhSentinelConfigPath;
    }
}
