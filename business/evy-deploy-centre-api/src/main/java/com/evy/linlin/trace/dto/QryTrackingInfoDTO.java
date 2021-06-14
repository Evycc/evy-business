package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.linlin.gateway.GatewayInputDTO;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:27
 */
public class QryTrackingInfoDTO extends GatewayInputDTO implements ValidatorDTO<QryTrackingInfoDTO> {
    /**
     * 唯一traceId
     */
    @NotBlank(message = "qryTraceId不能为空")
    private String qryTraceId;

    public QryTrackingInfoDTO() {
    }

    public QryTrackingInfoDTO(String qryTraceId) {
        this.qryTraceId = qryTraceId;
    }

    public String getQryTraceId() {
        return qryTraceId;
    }

    public void setQryTraceId(String qryTraceId) {
        this.qryTraceId = qryTraceId;
    }

    @Override
    public String toString() {
        return "QryTrackingInfoDTO{" +
                "qryTraceId='" + qryTraceId + '\'' +
                '}';
    }
}
