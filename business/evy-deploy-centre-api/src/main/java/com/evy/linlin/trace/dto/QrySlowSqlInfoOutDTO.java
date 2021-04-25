package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
public class QrySlowSqlInfoOutDTO extends OutDTO {
    private List<QrySlowSqlInfoModel> qrySlowSqlInfoList;

    public QrySlowSqlInfoOutDTO() {
    }

    @Override
    public String toString() {
        return "QrySlowSqlInfoOutDTO{" +
                "qrySlowSqlInfoList=" + qrySlowSqlInfoList +
                '}';
    }

    public List<QrySlowSqlInfoModel> getQrySlowSqlInfoList() {
        return qrySlowSqlInfoList;
    }

    public void setQrySlowSqlInfoList(List<QrySlowSqlInfoModel> qrySlowSqlInfoList) {
        this.qrySlowSqlInfoList = qrySlowSqlInfoList;
    }
}
