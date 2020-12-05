package com.evy.linlin.gateway;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 网关通用InputDTO
 * @Author: EvyLiuu
 * @Date: 2020/11/29 10:46
 */
@ToString
public class GatewayInputDTO extends InputDTO {
    /**
     * 服务码，对应Fegin服务实例Bean名，EvyGateway根据服务码路由到指定服务
     */
    @Getter
    @Setter
    private String serviceCode;
}
