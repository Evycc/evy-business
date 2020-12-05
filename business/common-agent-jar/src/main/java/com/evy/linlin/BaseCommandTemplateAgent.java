package com.evy.linlin;

import javassist.*;

import java.io.IOException;

/**
 * 添加出入参打印，方法耗时
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:27
 */
public class BaseCommandTemplateAgent {
    private static final String EXECUTE_METHOD = "com.evy.common.command.app.BaseCommandTemplate";

    /**
     * 通过copy原方法的内容到一个新方法，替换原方法body为 insertBodyBefore + 调用新方法 + insertBodyAfter，实现jvm级的AOP
     *
     * @return
     */
    public static byte[] agentExecute(String args) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(EXECUTE_METHOD);
            CtMethod[] ctMethods = ctClass.getDeclaredMethods();

            String methodName;
            String newMethodName = "JavaSsitAop";
            StringBuilder stringBuilder = new StringBuilder();
            for (CtMethod ctMethod : ctMethods) {
                methodName = ctMethod.getName();

                if (!filterMethod(methodName)) {
                    continue;
                }

                CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
                ctNewMethod.setName(methodName + newMethodName);
                ctClass.addMethod(ctNewMethod);
                if ("start".equals(methodName)) {
                    switchStart(stringBuilder, ctNewMethod);
                } else {
                    switchMethod(stringBuilder, ctNewMethod, methodName);
                }

                if (args.contains("DEBUG")) {
                    System.out.println("BaseCommandTemplateAgent#agentExecute\t" + stringBuilder);
                }

                ctMethod.setBody(stringBuilder.toString());

                stringBuilder.delete(0, stringBuilder.length());
            }
            ctClass.detach();
            return ctClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            if (args.contains("DEBUG")) {
                e.printStackTrace();
            }
            return new byte[0];
        }
    }

    /**
     * 只处理以下方法
     *
     * @param clsName 方法名
     * @return true 进行aop处理
     */
    private static boolean filterMethod(String clsName) {
        String methodName = "start|init|before|whenException|after";
        return methodName.contains(clsName);
    }

    /**
     * 处理普通方法，打印耗时及入参
     *
     * @param stringBuilder 方法体
     * @param ctMethod      方法
     */
    private static void switchMethod(StringBuilder stringBuilder, CtMethod ctMethod, String srcMethodName) {
        String mName = ctMethod.getName();
        CtClass[] params = null;
        CtClass returnType = null;
        try {
            returnType = ctMethod.getReturnType();
            params = ctMethod.getParameterTypes();
        } catch (NotFoundException ignored) {
        }
        String paramStr = "(String)\"null\"";
        if (params != null && params.length > 0) {
            paramStr = "$1";
        }

        stringBuilder.append("{")
                .append("long startTime = System.currentTimeMillis();")
                .append("com.evy.common.log.CommandLog.info(\"execute {} start. param:{}\",")
                .append("new Object[]{\"").append(srcMethodName).append("\",").append(paramStr).append("}")
                .append(");");
        if (returnType != null && returnType != CtClass.voidType) {
            stringBuilder.append("Object result = ");
        }
        //执行原方法
        stringBuilder
                .append(mName).append("($$);")
                .append("com.evy.common.log.CommandLog.info(\"execute {} end --[{}ms]\",")
                .append("new Object[]{\"")
                .append(srcMethodName)
                .append("\",Long.toString((System.currentTimeMillis() -startTime))});");

        if (returnType != null && !"void".equals(returnType.getName())) {
            //处理返回值
            stringBuilder.append("return ($r)result;");
        }
        stringBuilder.append("}");
    }

    /**
     * 针对com.evy.common.infrastructure.common.command.BaseCommandTemplate#start方法的特殊处理
     *
     * @param ctMethod BaseCommandTemplate#start
     */
    private static void switchStart(StringBuilder stringBuilder, CtMethod ctMethod) {
        String mName = ctMethod.getName();
        stringBuilder.append("{")
                .append("long startTime = System.currentTimeMillis();")
                //Trace记录开始
                .append("com.evy.common.trace.TraceLogUtils.setServiceTraceId(((com.evy.common.command.infrastructure.tunnel.dto.InputDTO)$1).getTraceId());")
                .append("com.evy.common.log.CommandLog.info(\"Start Service Flow...\");")
                //执行原方法
                .append("Object result = ").append(mName).append("($$);")
                //Trace记录结束
                .append("com.evy.common.trace.TraceLogUtils.rmLogTraceId();")
                .append("com.evy.common.log.CommandLog.info(\"Service return {}\",").append("new Object[]{").append("($r)result").append("});")
                .append("com.evy.common.log.CommandLog.info(\"End Service Flow--[{}ms]\",").append("new Object[]{Long.toString((System.currentTimeMillis() -startTime))});")
                .append("return ($r)result;")
                .append("}");
    }

    public static boolean judge(String className) {
        return EXECUTE_METHOD.equals(className);
    }
}
