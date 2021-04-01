package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:44
 */
public class QryTrackingInfoDO {
    private String traceId;

    public QryTrackingInfoDO(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "QryTrackingInfoDO{" +
                "traceId='" + traceId + '\'' +
                '}';
    }
}
