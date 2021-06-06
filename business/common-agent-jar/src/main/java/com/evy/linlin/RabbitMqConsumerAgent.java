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
     * true 已经进行agent false 未进行ange
     */
    private static boolean IS_AGENT = false;

    /**
     * com.evy.common.mq.rabbitmq.app.basic.BaseRabbitMqConsumer#doExecute
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
                    //Trace记录开始 START
                    //缓存当前线程traceId
                    .append("com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage sendMessage = $3;")
                    .append("String traceId = sendMessage.getPrpoMap().getOrDefault(\"traceId\", \"\");")
                    //设置当前线程为DB类型的traceId
                    .append("if(traceId != null && !\"\".equals(traceId)){")
                    .append("com.evy.common.trace.TraceLogUtils.rmLogTraceId(traceId);")
                    .append("}")
                    //Trace记录开始 END
                    .append(ctNewMethod.getName()).append("($$);")
                    .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                    .append("if(traceId != null && !\"\".equals(traceId)){")
                    .append("if(sendMessage != null) {")
                    .append("com.evy.common.trace.TraceLogUtils.setMqTraceId(traceId, false, sendMessage.getTopic(), sendMessage.getTag(), agentEndTime, agentStartTime);")
                    .append("}")
                    .append("com.evy.common.trace.TraceUtils.addTraceMqEnd(sendMessage.getMessageId(),agentEndTime);")
                    .append("}")
                    .append("}");
            if (args.contains("DEBUG")) {
                System.out.println("BaseRabbitMqConsumer#agentExecute\t" + stringBuilder);
            }
            ctMethod.setBody(stringBuilder.toString());
            ctClass.detach();
            return ctClass.toBytecode();
        } catch (Exception e) {
            if (args.contains("DEBUG")) {
                e.printStackTrace();
            }
            return new byte[0];
        } finally {
            IS_AGENT = true;
        }
    }

    public static boolean judge(String className) {
        return MQ_CONSUMER_STR.equals(className) && !IS_AGENT;
    }
}
