package com.evy.common.app.command;

import com.evy.common.infrastructure.common.command.BaseCommandTemplate;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;

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
