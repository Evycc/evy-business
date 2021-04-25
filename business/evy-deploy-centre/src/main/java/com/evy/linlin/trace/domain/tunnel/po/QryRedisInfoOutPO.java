package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:18
 */
public class QryRedisInfoOutPO {
    private String trhAppIp;
    private String trhRedisIp;
    private String trhRedisFlag;
    private String trhSlaveIp;
    private String trhClusterType;
    private String trhRdbOpen;
    private String trhRdbFile;
    private String trhRdbSaveType;
    private String trhAofOpen;
    private String trhAofFile;
    private String trhAofSaveType;
    private String trhAofRdbOpen;
    private String trhMemoryCount;
    private String trhMemoryAvailableCount;
    private String trhMemoryPeak;
    private String trhMemoryFragmentationCount;
    private String trhKeyspaceRatio;
    private String trhKeysCount;
    private String trhLastRdbStatus;
    private String trhLastAofStatus;
    private String trhLastForkUsec;
    private String trhConnTotalCount;
    private String trhConnCurCount;
    private String trhConnBlockCount;
    private String trhLogPath;
    private String trhConfigPath;
    private String trhSentinelMonitor;
    private String trhSentinelConfigPath;
    private String gmtModify;

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

    public String getTrhRdbOpen() {
        return trhRdbOpen;
    }

    public String getTrhRdbFile() {
        return trhRdbFile;
    }

    public String getTrhRdbSaveType() {
        return trhRdbSaveType;
    }

    public String getTrhAofOpen() {
        return trhAofOpen;
    }

    public String getTrhAofFile() {
        return trhAofFile;
    }

    public String getTrhAofSaveType() {
        return trhAofSaveType;
    }

    public String getTrhAofRdbOpen() {
        return trhAofRdbOpen;
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

    public String getTrhMemoryFragmentationCount() {
        return trhMemoryFragmentationCount;
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

    public String getGmtModify() {
        return gmtModify;
    }

    @Override
    public String toString() {
        return "QryRedisInfoOutPO{" +
                "trhAppIp='" + trhAppIp + '\'' +
                ", trhRedisIp='" + trhRedisIp + '\'' +
                ", trhRedisFlag='" + trhRedisFlag + '\'' +
                ", trhSlaveIp='" + trhSlaveIp + '\'' +
                ", trhClusterType='" + trhClusterType + '\'' +
                ", trhRdbOpen='" + trhRdbOpen + '\'' +
                ", trhRdbFile='" + trhRdbFile + '\'' +
                ", trhRdbSaveType='" + trhRdbSaveType + '\'' +
                ", trhAofOpen='" + trhAofOpen + '\'' +
                ", trhAofFile='" + trhAofFile + '\'' +
                ", trhAofSaveType='" + trhAofSaveType + '\'' +
                ", trhAofRdbOpen='" + trhAofRdbOpen + '\'' +
                ", trhMemoryCount='" + trhMemoryCount + '\'' +
                ", trhMemoryAvailableCount='" + trhMemoryAvailableCount + '\'' +
                ", trhMemoryPeak='" + trhMemoryPeak + '\'' +
                ", trhMemoryFragmentationCount='" + trhMemoryFragmentationCount + '\'' +
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
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }
}
