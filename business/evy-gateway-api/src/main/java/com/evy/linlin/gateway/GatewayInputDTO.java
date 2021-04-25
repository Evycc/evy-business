package com.evy.linlin.gateway;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;

/**
 * 网关通用InputDTO
 * @Author: EvyLiuu
 * @Date: 2020/11/29 10:46
 */
public class GatewayInputDTO extends InputDTO {
    /**
     * 服务码，对应Fegin服务实例Bean名，EvyGateway根据服务码路由到指定服务
     */
    private String serviceCode;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Override
    public String toString() {
        return "GatewayInputDTO{" +
                "serviceCode='" + serviceCode + '\'' +
                '}';
    }
}
