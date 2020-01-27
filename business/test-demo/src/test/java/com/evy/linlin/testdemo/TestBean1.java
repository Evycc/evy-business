package com.evy.linlin.testdemo;

import com.evy.common.infrastructure.common.command.BaseCommandTemplate;
import com.evy.common.infrastructure.common.log.anno.TraceLog;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
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
