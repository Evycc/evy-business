package com.evy.linlin;

import javassist.*;

import java.time.Duration;

/**
 * SpringCloud最后调用的Filter，对properties进行修改，用于控制服务超时时间
 *
 * @Author: EvyLiuu
 * @Date: 2021/6/14 14:08
 */
public class NettyRoutingFilterAgent {
    private final static String FILTER_METHOD = "org.springframework.cloud.gateway.filter.NettyRoutingFilter";
    private final static String TARGET_METHOD = "filter";
    /**
     * true 已经进行agent false 未进行ange
     */
    private static boolean IS_AGENT = false;

    public static byte[] agentExecute(String args) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(FILTER_METHOD);
            CtMethod ctMethod = ctClass.getDeclaredMethod(TARGET_METHOD);
            String methodName = ctMethod.getName();
            String newMethodName = "JavaSsitAop";
            StringBuilder stringBuilder = new StringBuilder();
            CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            ctNewMethod.setName(methodName + newMethodName);
            ctClass.addMethod(ctNewMethod);

            stringBuilder.append("{")
                    //从请求获取服务码，并设置超时时间 START
                    .append("org.springframework.web.server.ServerWebExchange swe = (org.springframework.web.server.ServerWebExchange) $1;")
                    .append("String srvCode = com.evy.linlin.gateway.filter.ServiceFilter.getSrvCode(swe)[0];")
                    .append("if(!org.springframework.util.StringUtils.isEmpty(srvCode)){")
                    .append("Integer timeout = com.evy.linlin.gateway.EvyGatewayRequestUtils.getSrvTimeout(srvCode);")
                    .append("com.evy.linlin.gateway.filter.ServiceFilter.modifyProperties($0.properties, timeout);")
//                    .append("$0.properties.setResponseTimeout(java.time.Duration.ofMillis((long)timeout));")
                    .append("}")
                    //从请求获取服务码，并设置超时时间 END
                    //继续调用原有方法 START
                    .append("return ").append(ctNewMethod.getName()).append("($$);")
                    //继续调用原有方法 END
                    .append("}");

            if (args.contains("DEBUG")) {
                System.out.println("NettyRoutingFilterAgent#agentExecute\t" + stringBuilder);
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
        return FILTER_METHOD.equals(className) && !IS_AGENT;
    }
}
