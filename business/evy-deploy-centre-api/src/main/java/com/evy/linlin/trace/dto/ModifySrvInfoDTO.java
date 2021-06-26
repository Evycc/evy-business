package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:42
 */
public class ModifySrvInfoDTO extends GatewayInputDTO implements ValidatorDTO<ModifySrvInfoDTO> {
    @NotBlank(message = "服务码为空")
    private String srvCode;
    @NotBlank(message = "服务名为空")
    private String serviceName;
    @NotBlank(message = "发布者应用为空")
    private String providerName;
    @NotBlank(message = "消费者应用为空")
    private String consumerName;
    private Integer limitQps;
    private String limitFallback;
    private Integer srvTimeout;

    @Override
    public String toString() {
        return "ModifySrvInfoDTO{" +
                "srvCode='" + srvCode + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", providerName='" + providerName + '\'' +
                ", consumerName='" + consumerName + '\'' +
                ", limitQps=" + limitQps +
                ", limitFallback='" + limitFallback + '\'' +
                ", srvTimeout=" + srvTimeout +
                '}';
    }

    public void setSrvCode(String srvCode) {
        this.srvCode = srvCode;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public void setLimitQps(Integer limitQps) {
        this.limitQps = limitQps;
    }

    public void setLimitFallback(String limitFallback) {
        this.limitFallback = limitFallback;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public Integer getLimitQps() {
        return limitQps;
    }

    public String getLimitFallback() {
        return limitFallback;
    }

    public Integer getSrvTimeout() {
        return srvTimeout;
    }

    public void setSrvTimeout(Integer srvTimeout) {
        this.srvTimeout = srvTimeout;
    }
}
