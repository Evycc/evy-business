package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
public class QryServiceInfoOutDTO extends GatewayOutDTO {
    private List<QryServiceInfoModel> qryServiceInfos;

    public QryServiceInfoOutDTO() {
    }

    @Override
    public String toString() {
        return "QryServiceInfoOutDTO{" +
                "qryServiceInfos=" + qryServiceInfos +
                '}';
    }

    public List<QryServiceInfoModel> getQryServiceInfos() {
        return qryServiceInfos;
    }

    public void setQryServiceInfos(List<QryServiceInfoModel> qryServiceInfos) {
        this.qryServiceInfos = qryServiceInfos;
    }
}
