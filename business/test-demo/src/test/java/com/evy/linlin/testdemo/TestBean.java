package com.evy.linlin.testdemo;

import com.evy.common.app.test.inceptor.TestInceptor;
import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.app.inceptor.anno.AnnoCommandInceptor;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
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
