package com.evy.common.app.command;

import com.evy.common.infrastructure.common.command.BaseCommandTemplate;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
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
