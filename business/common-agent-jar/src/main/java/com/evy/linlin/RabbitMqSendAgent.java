package com.evy.linlin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * 收集MQ发送者信息
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:39
 */
public class RabbitMqSendAgent {
    private final static String MQ_SENDER_STR = "com.evy.common.mq.rabbitmq.app.RabbitMqSender";
    /**
     * true 已经进行agent false 未进行ange
     */
    private static boolean IS_AGENT = false;

    /**
     * com.evy.common.mq.impl.RabbitMqSender#sendAndConfirm
     */
    public static byte[] agentExecute(String args) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(MQ_SENDER_STR);
            String newMethodName = "JavaSsitAop";
            CtMethod[] ctMethods = ctClass.getDeclaredMethods();

            String methodName;
            StringBuilder stringBuilder = new StringBuilder();
            String m = "sendAndConfirm";
            for (CtMethod ctMethod : ctMethods) {
                methodName = ctMethod.getName();
                if (m.equals(methodName)) {
                    CtClass[] ctClasses = ctMethod.getParameterTypes();
                    String p1 = "com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage";
                    String p3 = "com.rabbitmq.client.Channel";
                    if (ctClasses.length == 3 && p1.equals(ctClasses[0].getName()) &&
                            CtClass.booleanType == ctClasses[1] && p3.equals(ctClasses[2].getName())) {
                        CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
                        ctNewMethod.setName(methodName + newMethodName);
                        ctClass.addMethod(ctNewMethod);

                        stringBuilder.append("{long agentStartTime = System.currentTimeMillis();")
                                //Trace记录开始 START
                                //缓存当前线程traceId
                                .append("String traceId = com.evy.common.trace.TraceLogUtils.getCurTraceId();")
                                //设置当前线程为DB类型的traceId
                                .append("com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage mqMsg = $1;")
                                .append("if(traceId != null && !\"\".equals(traceId)){")
                                .append("if(mqMsg != null) {")
                                .append("mqMsg.getPrpoMap().put(\"traceId\", traceId);")
                                .append("}")
                                .append("}")
                                //Trace记录开始 END
                                .append("int result= ").append(ctNewMethod.getName()).append("($$);")
                                .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                                .append("com.evy.common.trace.TraceUtils.addTraceMqSend($1);")
//                                .append("com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage mqMsg = $1;")
                                //Trace记录结束 START
                                .append("if(traceId != null && !\"\".equals(traceId)){")
                                .append("if(mqMsg != null) {")
                                .append("com.evy.common.trace.TraceLogUtils.setMqTraceId(traceId, true, mqMsg.getTopic(), mqMsg.getTag(), agentEndTime, agentStartTime);")
                                .append("} else {")
                                .append("com.evy.common.trace.TraceLogUtils.setMqTraceId(traceId, true, \"\", \"\", agentEndTime, agentStartTime);")
                                .append("}")
                                .append("}")
                                //Trace记录结束 END
                                .append("return result;}");
                        if (args.contains("DEBUG")) {
                            System.out.println("RabbitMqSendAgent#agentExecute\t" + stringBuilder);
                        }
                        ctMethod.setBody(stringBuilder.toString());
                        break;
                    }
                }
            }
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
        return MQ_SENDER_STR.equals(className);
    }
}
