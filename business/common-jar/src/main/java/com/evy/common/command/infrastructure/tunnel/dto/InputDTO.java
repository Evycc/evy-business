package com.evy.common.command.infrastructure.tunnel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 接口入参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@ToString
public class InputDTO implements Serializable {
    private static final long serialVersionUID = 546422L;
    @Getter
    @Setter
    @NotNull(message = "srcSendNo不能为空")
    private String srcSendNo;
    @Getter
    @Setter
    @NotNull(message = "requestTime不能为空")
    private String requestTime;
    @Getter
    @Setter
    @NotNull(message = "clientIp不能为空")
    private String clientIp;
    @Getter
    @Setter
    private String traceId;
}
