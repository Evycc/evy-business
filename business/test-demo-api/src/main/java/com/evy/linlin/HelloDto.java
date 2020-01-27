package com.evy.linlin;

import com.evy.common.app.validator.ValidatorDTO;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.test.TestInput;
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
