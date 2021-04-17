package com.evy.linlin;

import java.lang.instrument.Instrumentation;

/**
 * 添加jvm级别的agent功能<br/>
 * 为com.evy.common.infrastructure.common.command.BaseCommandTemplate添加出入参日志打印<br/>
 * DB慢SQL收集<br/>
 * MQ发送/消费链路收集<br/>
 * Http请求耗时统计<br/>
 *
 */
public class AgentApp {
    /**
     * agent
     * @param agentArg  DEBUG : 打印修改后字节码字符串 SLOW_SQL=1000
     * @param instrumentation   java.lang.instrument.Instrumentation
     */
    public static void premain(String agentArg, Instrumentation instrumentation)
    {
        System.out.println("Agent Start.");
        //agentArg=DEBUG    打印替换后的字节码字符串
        instrumentation.addTransformer(new AgentApdater(agentArg), true);
    }
}
