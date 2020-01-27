package com.evy.common.app.inceptor;

import com.evy.common.infrastructure.common.inceptor.BaseCommandInceptor;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.tunnel.test.TestInput;

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
