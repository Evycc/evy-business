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
     * 备注
     */
    private String remarks;
    /**
     * 调用顺序
     */
    private Integer order;

    public QryTrackingInfoModel(String qryTraceId, String reqType, String takeTimeMs, String remarks, Integer order) {
        this.qryTraceId = qryTraceId;
        this.reqType = reqType;
        this.takeTimeMs = takeTimeMs;
        this.remarks = remarks;
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
