package com.evy.linlin.testdemo;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@TraceLog
public class TestBean1 extends BaseCommandTemplate {
    private final static Logger log = LoggerFactory.getLogger(TestBean1.class);
    @Override
    public OutDTO execute(InputDTO inputDTO) {
        log.info("TestBean1");
        return null;
    }
}
