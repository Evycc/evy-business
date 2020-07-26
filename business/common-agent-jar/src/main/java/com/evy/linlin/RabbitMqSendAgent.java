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
                                .append("int result= ").append(ctNewMethod.getName()).append("($$);")
                                .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                                .append("com.evy.common.trace.TraceUtils.addTraceMqSend($1);")
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
        }
    }

    public static boolean judge(String classNmae) {
        return MQ_SENDER_STR.equals(classNmae);
    }
}
