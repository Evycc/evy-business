package com.evy.common.app.inceptor;

import com.evy.common.infrastructure.common.inceptor.BaseCommandInceptor;
import com.evy.common.infrastructure.tunnel.test.TestInput;

/**
 * @Author: EvyLiuu
 * @Date: 2019/10/26 23:44
 */
public class TestInceptor1 extends BaseCommandInceptor<TestInput> {
    @Override
    public int beforeCommand(TestInput testInput) {
        return 0;
    }

    @Override
    public int afterCommand(TestInput testInput) {
        return 0;
    }
}
