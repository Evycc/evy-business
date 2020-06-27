package com.evy.common.app.test.command;

import com.evy.common.app.test.inceptor.TestInceptor;
import com.evy.common.app.test.inceptor.TestInceptor1;
import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.app.inceptor.anno.AnnoCommandInceptor;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.infrastructure.tunnel.test.TestInput;
import com.evy.common.infrastructure.tunnel.test.TestOutDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 交易执行类
 * @Author: EvyLiuu
 * @Date: 2019/10/22 23:27
 */
@AnnoCommandInceptor(proxyClass = {TestInceptor.class, TestInceptor1.class})
@Component
@TraceLog
public class CommandExecute extends BaseCommandTemplate<TestInput, TestOutDTO> {
    private static Logger logger = LoggerFactory.getLogger(CommandExecute.class);

    @Override
    public TestOutDTO execute(TestInput testInput) throws BasicException {
        logger.info("tttt");
        CommandLog.info("CommandExecute execute");

        TestCommandExecute testCommandExecute = new TestCommandExecute();
        testCommandExecute.start(testInput);

        TestOutDTO outDTO = new TestOutDTO();

        return outDTO;
    }
}
