package com.evy.linlin.testdemo;

import com.evy.common.app.inceptor.TestInceptor;
import com.evy.common.infrastructure.common.inceptor.anno.AnnoCommandInceptor;
import com.evy.common.infrastructure.common.command.BaseCommandTemplate;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
import org.springframework.stereotype.Component;

@AnnoCommandInceptor(proxyClass = {TestInceptor.class})
@Component
public class TestBean extends BaseCommandTemplate {
    @Override
    public OutDTO execute(InputDTO inputDTO) {
        System.out.println("TestBean");
        return null;
    }
}
