package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:24
 */
public class QryRedisInfoModel extends CommandModel {
    private String appIp;
    private String redisIp;
    /**
     * 主从标识,0主1从
     */
    private String redisFlag;
    /**
     * 从机器ip
     */
    private String slaveIp;
    /**
     * redis类型
     */
    private String clusterType;
    /**
     * rdb是否开启,1开启
     */
    private String rdbOpen;
    /**
     * RDB文件路径
     */
    private String rdbFile;
    /**
     * rdb保存规则
     */
    private String rdbSaveType;
    /**
     * aof是否开启,1是
     */
    private String aofOpen;
    /**
     * aof文件路径
     */
    private String aofFile;
    /**
     * aof保存规则
     */
    private String aofSaveType;
    /**
     * 混合持久化是否开启,1开启
     */
    private String aofRdbOpen;
    /**
     * 服务器总内存,单位kb
     */
    private String mermoryCountKb;
    /**
     * redis占用内存,单位kb
     */
    private String mermoryAvailableCountKb;
    /**
     * redis占用内存峰值,单位kb
     */
    private String mermoryPeakKb;
    /**
     * 内存碎片率
     */
    private String mermoryFragmentationRatio;
    /**
     * redis命中率
     */
    private String keyspaceRatio;
    /**
     * key总数
     */
    private String keysCount;
    /**
     * 最近一次rdb状态
     */
    private String lastRdbStatus;
    /**
     * 最近一次aof状态
     */
    private String lastAofStatus;
    /**
     * 上次fork阻塞时间，单位微秒
     */
    private String lastForkUsec;
    /**
     * redis最大连接数
     */
    private String connTotalCount;
    /**
     * 当前连接数
     */
    private String connCurCount;
    /**
     * 当前阻塞连接数
     */
    private String connBlockCount;
    /**
     * redis日志路径
     */
    private String redisLogPath;
    /**
     * redis配置文件路径
     */
    private String redisConfigPath;
    /**
     * redis哨兵列表
     */
    private String sentinelMonitor;
    /**
     * 哨兵配置文件路径
     */
    private String sentinelConfigPath;

    public QryRedisInfoModel() {
    }

    public QryRedisInfoModel(String appIp, String redisIp, String redisFlag, String slaveIp, String clusterType, String rdbOpen, String rdbFile, String rdbSaveType, String aofOpen, String aofFile, String aofSaveType, String aofRdbOpen, String mermoryCountKb, String mermoryAvailableCountKb, String mermoryPeakKb, String mermoryFragmentationRatio, String keyspaceRatio, String keysCount, String lastRdbStatus, String lastAofStatus, String lastForkUsec, String connTotalCount, String connCurCount, String connBlockCount, String redisLogPath, String redisConfigPath, String sentinelMonitor, String sentinelConfigPath) {
        this.appIp = appIp;
        this.redisIp = redisIp;
        this.redisFlag = redisFlag;
        this.slaveIp = slaveIp;
        this.clusterType = clusterType;
        this.rdbOpen = rdbOpen;
        this.rdbFile = rdbFile;
        this.rdbSaveType = rdbSaveType;
        this.aofOpen = aofOpen;
        this.aofFile = aofFile;
        this.aofSaveType = aofSaveType;
        this.aofRdbOpen = aofRdbOpen;
        this.mermoryCountKb = mermoryCountKb;
        this.mermoryAvailableCountKb = mermoryAvailableCountKb;
        this.mermoryPeakKb = mermoryPeakKb;
        this.mermoryFragmentationRatio = mermoryFragmentationRatio;
        this.keyspaceRatio = keyspaceRatio;
        this.keysCount = keysCount;
        this.lastRdbStatus = lastRdbStatus;
        this.lastAofStatus = lastAofStatus;
        this.lastForkUsec = lastForkUsec;
        this.connTotalCount = connTotalCount;
        this.connCurCount = connCurCount;
        this.connBlockCount = connBlockCount;
        this.redisLogPath = redisLogPath;
        this.redisConfigPath = redisConfigPath;
        this.sentinelMonitor = sentinelMonitor;
        this.sentinelConfigPath = sentinelConfigPath;
    }

    @Override
    public String toString() {
        return "QryRedisInfoModel{" +
                "appIp='" + appIp + '\'' +
                ", redisIp='" + redisIp + '\'' +
                ", redisFlag='" + redisFlag + '\'' +
                ", slaveIp='" + slaveIp + '\'' +
                ", clusterType='" + clusterType + '\'' +
                ", rdbOpen='" + rdbOpen + '\'' +
                ", rdbFile='" + rdbFile + '\'' +
                ", rdbSaveType='" + rdbSaveType + '\'' +
                ", aofOpen='" + aofOpen + '\'' +
                ", aofFile='" + aofFile + '\'' +
                ", aofSaveType='" + aofSaveType + '\'' +
                ", aofRdbOpen='" + aofRdbOpen + '\'' +
                ", mermoryCountKb='" + mermoryCountKb + '\'' +
                ", mermoryAvailableCountKb='" + mermoryAvailableCountKb + '\'' +
                ", mermoryPeakKb='" + mermoryPeakKb + '\'' +
                ", mermoryFragmentationRatio='" + mermoryFragmentationRatio + '\'' +
                ", keyspaceRatio='" + keyspaceRatio + '\'' +
                ", keysCount='" + keysCount + '\'' +
                ", lastRdbStatus='" + lastRdbStatus + '\'' +
                ", lastAofStatus='" + lastAofStatus + '\'' +
                ", lastForkUsec='" + lastForkUsec + '\'' +
                ", connTotalCount='" + connTotalCount + '\'' +
                ", connCurCount='" + connCurCount + '\'' +
                ", connBlockCount='" + connBlockCount + '\'' +
                ", redisLogPath='" + redisLogPath + '\'' +
                ", redisConfigPath='" + redisConfigPath + '\'' +
                ", sentinelMonitor='" + sentinelMonitor + '\'' +
                ", sentinelConfigPath='" + sentinelConfigPath + '\'' +
                '}';
    }

    public String getAppIp() {
        return appIp;
    }

    public String getRedisIp() {
        return redisIp;
    }

    public String getRedisFlag() {
        return redisFlag;
    }

    public String getSlaveIp() {
        return slaveIp;
    }

    public String getClusterType() {
        return clusterType;
    }

    public String getRdbOpen() {
        return rdbOpen;
    }

    public String getRdbFile() {
        return rdbFile;
    }

    public String getRdbSaveType() {
        return rdbSaveType;
    }

    public String getAofOpen() {
        return aofOpen;
    }

    public String getAofFile() {
        return aofFile;
    }

    public String getAofSaveType() {
        return aofSaveType;
    }

    public String getAofRdbOpen() {
        return aofRdbOpen;
    }

    public String getMermoryCountKb() {
        return mermoryCountKb;
    }

    public String getMermoryAvailableCountKb() {
        return mermoryAvailableCountKb;
    }

    public String getMermoryPeakKb() {
        return mermoryPeakKb;
    }

    public String getMermoryFragmentationRatio() {
        return mermoryFragmentationRatio;
    }

    public String getKeyspaceRatio() {
        return keyspaceRatio;
    }

    public String getKeysCount() {
        return keysCount;
    }

    public String getLastRdbStatus() {
        return lastRdbStatus;
    }

    public String getLastAofStatus() {
        return lastAofStatus;
    }

    public String getLastForkUsec() {
        return lastForkUsec;
    }

    public String getConnTotalCount() {
        return connTotalCount;
    }

    public String getConnCurCount() {
        return connCurCount;
    }

    public String getConnBlockCount() {
        return connBlockCount;
    }

    public String getRedisLogPath() {
        return redisLogPath;
    }

    public String getRedisConfigPath() {
        return redisConfigPath;
    }

    public String getSentinelMonitor() {
        return sentinelMonitor;
    }

    public String getSentinelConfigPath() {
        return sentinelConfigPath;
    }
}
