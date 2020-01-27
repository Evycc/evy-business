package com.evy.common.infrastructure.tunnel.test;

import com.evy.common.app.validator.ValidatorDTO;
import com.evy.common.infrastructure.tunnel.InputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class TestInput extends InputDTO implements ValidatorDTO<TestInput> {
    @Getter
    @Setter
    private String test;
}
