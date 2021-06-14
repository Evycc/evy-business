package com.evy.linlin;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对指定类进行Agent
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:13
 */
public class AgentApdater implements ClassFileTransformer {
    private static final String POINT = ".";
    private static final String SPLIT = "/";
    private String args;
    private static final Pattern PATTERN = Pattern.compile("SLOW_SQL=(\\d)+");

    public AgentApdater(String args) {
        this.args = args;
    }

    /**
     * 在此重新定义类<br/>
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            String clsName = className.replaceAll(SPLIT, POINT);

            if (BaseCommandTemplateAgent.judge(clsName)) {
                return BaseCommandTemplateAgent.agentExecute(args);
            } else if (RabbitMqSendAgent.judge(clsName)) {
                return RabbitMqSendAgent.agentExecute(args);
            }else if (RabbitMqConsumerAgent.judge(clsName)) {
                return RabbitMqConsumerAgent.agentExecute(args);
            } else if (DBUtilsAgent.judge(clsName)) {
                Matcher matcher = PATTERN.matcher(args);
                //默认1s
                int slowSqlTime = 1000;
                if (matcher.find()) {
                    String var = matcher.group(0);
                    try {
                        slowSqlTime = Integer.parseInt(var.substring(var.indexOf("=") +1));
                        System.out.println("Agent DataBase. 慢SQL监控阈值(毫秒):" + slowSqlTime);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                return DBUtilsAgent.agentExecute(args, slowSqlTime);
            } else if (NettyRoutingFilterAgent.judge(clsName)) {
                return NettyRoutingFilterAgent.agentExecute(args);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new byte[0];
    }
}
