package com.evy.linlin.trace.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:07
 */
public class QryHttpInfoOutDTO extends GatewayOutDTO {
    private List<QryHttpInfoModel> list;

    public QryHttpInfoOutDTO() {
    }

    @Override
    public String toString() {
        return "QryHttpInfoOutDTO{" +
                "list=" + list +
                '}';
    }

    public List<QryHttpInfoModel> getList() {
        return list;
    }

    public void setList(List<QryHttpInfoModel> list) {
        this.list = list;
    }
}
