package com.evy.linlin.trace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:24
 */
@ToString
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class QryRedisInfoModel {
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
}
