package com.evy.linlin.trace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 12:06
 */
@ToString
@AllArgsConstructor
@Getter
public class QryAppMermoryInfoModel {
    /**
     * 应用IP
     */
    private String appIp;
    /**
     * CPU可用核心数
     */
    private String cpuCount;
    /**
     * CPU使用率
     */
    private String cpuLoadPercentage;
    /**
     * 系统内存,单位kb
     */
    private String sysMermoryKb;
    /**
     * 系统可用内存,单位kb
     */
    private String sysAvailMermoryKb;
    /**
     * 系统已用内存,单位kb
     */
    private String sysUseMermoryKb;
    /**
     * 应用最大堆内存,单位kb
     */
    private String appHeapMaxMermoryKb;
    /**
     * 应用最小堆内存,单位kb
     */
    private String appHeapMinMermoryKb;
    /**
     * 应用占用堆内存,单位kb
     */
    private String appHeapUseMermoryKb;
    /**
     * 应用最大非堆内存,单位kb
     */
    private String appNoHeapMaxMermoryKb;
    /**
     * 应用最小非堆内存,单位kb
     */
    private String appNoHeapMinMermoryKb;
    /**
     * 应用占用非堆内存,单位kb
     */
    private String appNoHeapUseMermoryKb;
    /**
     * 采集时间
     */
    private String gmtModify;
}
