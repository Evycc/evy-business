package com.evy.common.app.test.inceptor;

import com.evy.common.command.app.inceptor.BaseCommandInceptor;
import com.evy.common.infrastructure.tunnel.test.TestInput;
import com.evy.common.log.CommandLog;

public class TestInceptor extends BaseCommandInceptor<TestInput> {

    @Override
    public int beforeCommand(TestInput testInput) {
        CommandLog.info("beforeCommand");
        return 0;
    }

    @Override
    public int afterCommand(TestInput testInput) {
        CommandLog.info("afterCommand");
        return 0;
    }
}
