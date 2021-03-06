package com.evy.linlin.trace.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 12:00
 */
public class QryAppMermoryInfoOutDTO extends GatewayOutDTO {
    private Map<String, List<QryAppMermoryInfoModel>> outMap;

    public QryAppMermoryInfoOutDTO() {
    }

    public Map<String, List<QryAppMermoryInfoModel>> getOutMap() {
        return outMap;
    }

    public void setOutMap(Map<String, List<QryAppMermoryInfoModel>> outMap) {
        this.outMap = outMap;
    }
}
