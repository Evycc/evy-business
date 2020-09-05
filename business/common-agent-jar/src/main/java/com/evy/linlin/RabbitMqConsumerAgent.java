package com.evy.linlin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:39
 */
public class RabbitMqConsumerAgent {
    private static final String MQ_CONSUMER_STR = "com.evy.common.mq.rabbitmq.app.basic.BaseRabbitMqConsumer";

    /**
     * com.evy.common.mq.basic.BaseRabbitMqConsumer#doExecute
     */
    public static byte[] agentExecute(String args) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(MQ_CONSUMER_STR);
            CtMethod ctMethod = ctClass.getDeclaredMethod("doExecute");
            String methodName = ctMethod.getName();
            String newMethodName = "JavaSsitAop";
            StringBuilder stringBuilder = new StringBuilder();
            CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            ctNewMethod.setName(methodName + newMethodName);
            ctClass.addMethod(ctNewMethod);

            stringBuilder.append("{long agentStartTime = System.currentTimeMillis();")
                    .append(ctNewMethod.getName()).append("($$);")
                    .append("com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage sendMessage")
                    .append("= (com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage) org.springframework.util.SerializationUtils.deserialize($4);")
                    .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                    .append("com.evy.common.trace.TraceUtils.addTraceMqEnd(sendMessage.getMessageId(),agentEndTime);")
                    .append("}");
            if (args.contains("DEBUG")) {
                System.out.println("DBUtilsAgent#agentExecute\t" + stringBuilder);
            }
            ctMethod.setBody(stringBuilder.toString());
            ctClass.detach();
            return ctClass.toBytecode();
        } catch (Exception e) {
            if (args.contains("DEBUG")) {
                e.printStackTrace();
            }
            return new byte[0];
        }
    }

    public static boolean judge(String classNmae) {
        return MQ_CONSUMER_STR.equals(classNmae);
    }
}