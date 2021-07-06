package com.evy.linlin.trace.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
public class QrySlowSqlInfoOutDTO extends GatewayOutDTO {
    private List<QrySlowSqlInfoModel> qrySlowSqlInfoList;

    public QrySlowSqlInfoOutDTO() {
    }

    public List<QrySlowSqlInfoModel> getQrySlowSqlInfoList() {
        return qrySlowSqlInfoList;
    }

    public void setQrySlowSqlInfoList(List<QrySlowSqlInfoModel> qrySlowSqlInfoList) {
        this.qrySlowSqlInfoList = qrySlowSqlInfoList;
    }
}
