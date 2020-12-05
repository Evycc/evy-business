package com.evy.linlin;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 17:50
 */
@ToString
public class HelloDto extends GatewayInputDTO implements ValidatorDTO<HelloDto> {
    @Getter
    @Setter
    String helloId;
}
