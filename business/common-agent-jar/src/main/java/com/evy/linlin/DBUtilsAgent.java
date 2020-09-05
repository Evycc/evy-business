package com.evy.linlin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * 监听mysql底层，统计慢sql
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:40
 */
public class DBUtilsAgent {
    private final static String DB_METHOD = "com.mysql.cj.NativeSession";

    /**
     * 监听mysql底层com.mysql.cj.NativeSession#execSQL(Query callingQuery, String query, int maxRows,
     * NativePacketPayload packet, boolean streamResults, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory,
     * String catalog, ColumnDefinition cachedMetadata, boolean isBatch)
     */
    public static byte[] agentExecute(String args) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(DB_METHOD);
            CtMethod ctMethod = ctClass.getDeclaredMethod("execSQL");
            String methodName = ctMethod.getName();
            String newMethodName = "JavaSsitAop";
            StringBuilder stringBuilder = new StringBuilder();
            CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            ctNewMethod.setName(methodName + newMethodName);
            ctClass.addMethod(ctNewMethod);

            stringBuilder.append("{long agentStartTime = System.currentTimeMillis();")
                    .append("Object result= ").append(ctNewMethod.getName()).append("($$);")
                    .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                    .append("if(null != $1 && agentEndTime >= 1000){")
                    .append("final String pre = \"com.mysql.cj.jdbc.ClientPreparedStatement: \";")
                    .append("String slowSql = String.valueOf($1);")
                    .append("slowSql = slowSql.replaceAll(pre,\"\");")
                    .append("com.evy.common.trace.TraceUtils.addTraceSql(slowSql,agentEndTime);}")
                    .append("return ($r)result;")
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
        return DB_METHOD.equals(classNmae);
    }
}