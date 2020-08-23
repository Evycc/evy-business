package com.evy.linlin;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 17:50
 */
public class HelloDto extends InputDTO implements ValidatorDTO<HelloDto> {
    @Getter
    @Setter
    String helloId;
}
