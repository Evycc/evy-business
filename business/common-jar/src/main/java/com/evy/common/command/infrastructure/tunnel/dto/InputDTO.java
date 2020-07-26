package com.evy.common.command.infrastructure.tunnel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 接口入参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@ToString
public class InputDTO{
    @Getter
    @Setter
    @NotNull
    private String srcSendNo;
    @Getter
    @Setter
    @NotNull
    private String requestTime;
    @Getter
    @Setter
    @NotNull
    private String clientIp;
    @Getter
    @Setter
    private String traceId;
}
