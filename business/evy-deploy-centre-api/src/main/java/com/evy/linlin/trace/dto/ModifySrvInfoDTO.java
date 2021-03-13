package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:42
 */
@Getter
@ToString
public class ModifySrvInfoDTO extends InputDTO implements ValidatorDTO<ModifySrvInfoDTO> {
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
}
