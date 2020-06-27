package com.evy.common.app.test.command;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;

/**
 * @Author: EvyLiuu
 * @Date: 2019/10/26 12:39
 */
public class TestCommandExecute1 extends BaseCommandTemplate {
    @Override
    public OutDTO execute(InputDTO inputDTO) {
        CommandLog.info("s1111111111111111111");
        return null;
    }
}
