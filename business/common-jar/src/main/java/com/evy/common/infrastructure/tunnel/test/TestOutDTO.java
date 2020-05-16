package com.evy.common.infrastructure.tunnel.test;

import com.evy.common.infrastructure.tunnel.OutDTO;
import lombok.Getter;
import lombok.Setter;

public class TestOutDTO extends OutDTO {
    @Getter
    @Setter
    String testOut;

    @Override
    public String toString() {
        super.toString();
        return "TestOutDTO{" +
                "testOut='" + testOut + '\'' +
                '}';
    }
}
