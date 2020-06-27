package com.evy.common.app.test.command;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: EvyLiuu
 * @Date: 2019/10/26 10:14
 */
public class TestCommandExecute extends BaseCommandTemplate {
    private final static Logger log = LoggerFactory.getLogger("com.evy.common.app.command.TestCommandExecute");
    @Override
    public OutDTO execute(InputDTO inputDTO) {
        CommandLog.info("ssssssssssssssss");
        new TestCommandExecute1().execute(inputDTO);
        return null;
    }
}
