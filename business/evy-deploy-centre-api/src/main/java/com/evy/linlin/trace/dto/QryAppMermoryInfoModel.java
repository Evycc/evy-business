package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 12:06
 */
public class QryAppMermoryInfoModel extends CommandModel {
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

    public QryAppMermoryInfoModel(String appIp, String cpuCount, String cpuLoadPercentage, String sysMermoryKb, String sysAvailMermoryKb, String sysUseMermoryKb, String appHeapMaxMermoryKb, String appHeapMinMermoryKb, String appHeapUseMermoryKb, String appNoHeapMaxMermoryKb, String appNoHeapMinMermoryKb, String appNoHeapUseMermoryKb, String gmtModify) {
        this.appIp = appIp;
        this.cpuCount = cpuCount;
        this.cpuLoadPercentage = cpuLoadPercentage;
        this.sysMermoryKb = sysMermoryKb;
        this.sysAvailMermoryKb = sysAvailMermoryKb;
        this.sysUseMermoryKb = sysUseMermoryKb;
        this.appHeapMaxMermoryKb = appHeapMaxMermoryKb;
        this.appHeapMinMermoryKb = appHeapMinMermoryKb;
        this.appHeapUseMermoryKb = appHeapUseMermoryKb;
        this.appNoHeapMaxMermoryKb = appNoHeapMaxMermoryKb;
        this.appNoHeapMinMermoryKb = appNoHeapMinMermoryKb;
        this.appNoHeapUseMermoryKb = appNoHeapUseMermoryKb;
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "QryAppMermoryInfoModel{" +
                "appIp='" + appIp + '\'' +
                ", cpuCount='" + cpuCount + '\'' +
                ", cpuLoadPercentage='" + cpuLoadPercentage + '\'' +
                ", sysMermoryKb='" + sysMermoryKb + '\'' +
                ", sysAvailMermoryKb='" + sysAvailMermoryKb + '\'' +
                ", sysUseMermoryKb='" + sysUseMermoryKb + '\'' +
                ", appHeapMaxMermoryKb='" + appHeapMaxMermoryKb + '\'' +
                ", appHeapMinMermoryKb='" + appHeapMinMermoryKb + '\'' +
                ", appHeapUseMermoryKb='" + appHeapUseMermoryKb + '\'' +
                ", appNoHeapMaxMermoryKb='" + appNoHeapMaxMermoryKb + '\'' +
                ", appNoHeapMinMermoryKb='" + appNoHeapMinMermoryKb + '\'' +
                ", appNoHeapUseMermoryKb='" + appNoHeapUseMermoryKb + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }

    public String getAppIp() {
        return appIp;
    }

    public String getCpuCount() {
        return cpuCount;
    }

    public String getCpuLoadPercentage() {
        return cpuLoadPercentage;
    }

    public String getSysMermoryKb() {
        return sysMermoryKb;
    }

    public String getSysAvailMermoryKb() {
        return sysAvailMermoryKb;
    }

    public String getSysUseMermoryKb() {
        return sysUseMermoryKb;
    }

    public String getAppHeapMaxMermoryKb() {
        return appHeapMaxMermoryKb;
    }

    public String getAppHeapMinMermoryKb() {
        return appHeapMinMermoryKb;
    }

    public String getAppHeapUseMermoryKb() {
        return appHeapUseMermoryKb;
    }

    public String getAppNoHeapMaxMermoryKb() {
        return appNoHeapMaxMermoryKb;
    }

    public String getAppNoHeapMinMermoryKb() {
        return appNoHeapMinMermoryKb;
    }

    public String getAppNoHeapUseMermoryKb() {
        return appNoHeapUseMermoryKb;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
