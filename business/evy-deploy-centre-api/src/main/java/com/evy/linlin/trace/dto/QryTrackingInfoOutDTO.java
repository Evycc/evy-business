package com.evy.linlin.trace.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:31
 */
public class QryTrackingInfoOutDTO extends GatewayOutDTO {
    private List<QryTrackingInfoModel> traceList;

    public QryTrackingInfoOutDTO() {
    }

    public QryTrackingInfoOutDTO(List<QryTrackingInfoModel> traceList) {
        this.traceList = traceList;
    }

    public List<QryTrackingInfoModel> getTraceList() {
        return traceList;
    }

    public void setTraceList(List<QryTrackingInfoModel> traceList) {
        this.traceList = traceList;
    }

    @Override
    public String toString() {
        return "QryTrackingInfoOutDTO{" +
                "traceList=" + traceList +
                '}';
    }
}
