package com.evy.linlin;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 对指定类进行Agent
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:13
 */
public class AgentApdater implements ClassFileTransformer {
    private static final String POINT = ".";
    private static final String SPLIT = "/";
    private String args;

    public AgentApdater(String args) {
        this.args = args;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String clsName = className.replaceAll(SPLIT, POINT);
        if (BaseCommandTemplateAgent.judge(clsName)) {
            return BaseCommandTemplateAgent.agentExecute(args);
        } else if (RabbitMqSendAgent.judge(clsName)) {
            return RabbitMqSendAgent.agentExecute(args);
        }else if (RabbitMqConsumerAgent.judge(clsName)) {
            return RabbitMqConsumerAgent.agentExecute(args);
        } else if (DBUtilsAgent.judge(clsName)) {
            return DBUtilsAgent.agentExecute(args);
        }
        return new byte[0];
    }
}
