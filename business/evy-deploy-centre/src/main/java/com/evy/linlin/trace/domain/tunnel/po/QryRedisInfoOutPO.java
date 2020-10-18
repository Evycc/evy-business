package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:18
 */
@Getter
@ToString
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
}
