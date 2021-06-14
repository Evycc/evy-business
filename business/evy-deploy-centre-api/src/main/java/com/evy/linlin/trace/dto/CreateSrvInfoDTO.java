package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.linlin.gateway.GatewayInputDTO;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:39
 */
public class CreateSrvInfoDTO extends GatewayInputDTO implements ValidatorDTO<CreateSrvInfoDTO> {
    @NotBlank(message = "服务码为空")
    private String srvCode;
    @NotBlank(message = "发布者应用为空")
    private String providerName;
    @NotBlank(message = "消费者应用为空")
    private String consumerName;

    @Override
    public String toString() {
        return "CreateSrvInfoDTO{" +
                "srvCode='" + srvCode + '\'' +
                ", providerName='" + providerName + '\'' +
                ", consumerName='" + consumerName + '\'' +
                '}';
    }

    public void setSrvCode(String srvCode) {
        this.srvCode = srvCode;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getConsumerName() {
        return consumerName;
    }
}
