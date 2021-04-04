package com.evy.linlin.trace.dto;

/**
 * trace链路模型
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:32
 */
public class QryTrackingInfoModel {
    /**
     * 唯一traceId
     */
    private String qryTraceId;
    /**
     * 请求类型
     */
    private String reqType;
    /**
     * 耗时ms
     */
    private String takeTimeMs;
    /**
     * 服务调用类型、DB类型、MQ类型用<br/>
     * 服务调用类型:服务码<br/>
     * DB类型:数据库名<br/>
     * MQ类型:topic+tag
     */
    private String remarks;
    /**
     * 调用顺序
     */
    private Long order;
    /**
     * MQ类型用,true:发布者 false:消费者
     */
    private Boolean isProvider;
    /**
     * 应用名
     */
    private String appName;

    public QryTrackingInfoModel(String qryTraceId, String reqType, String takeTimeMs, String remarks, Long order, Boolean isProvider, String appName) {
        this.qryTraceId = qryTraceId;
        this.reqType = reqType;
        this.takeTimeMs = takeTimeMs;
        this.remarks = remarks;
        this.order = order;
        this.isProvider = isProvider;
        this.appName = appName;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Boolean getProvider() {
        return isProvider;
    }

    public void setProvider(Boolean provider) {
        isProvider = provider;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getQryTraceId() {
        return qryTraceId;
    }

    public void setQryTraceId(String qryTraceId) {
        this.qryTraceId = qryTraceId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getTakeTimeMs() {
        return takeTimeMs;
    }

    public void setTakeTimeMs(String takeTimeMs) {
        this.takeTimeMs = takeTimeMs;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "QryTrackingInfoModel{" +
                "qryTraceId='" + qryTraceId + '\'' +
                ", reqType='" + reqType + '\'' +
                ", takeTimeMs='" + takeTimeMs + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
