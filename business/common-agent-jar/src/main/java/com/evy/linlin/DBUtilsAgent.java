package com.evy.linlin;

import javassist.*;

/**
 * 监听mysql底层，统计慢sql
 * @Author: EvyLiuu
 * @Date: 2020/5/31 15:40
 */
public class DBUtilsAgent {
    private final static String DB_METHOD = "com.mysql.cj.NativeSession";
    private final static String DB_UTILS_METHOD = "com.evy.common.db.DBUtils";

    /**
     * 监听mysql底层com.mysql.cj.NativeSession#execSQL(Query callingQuery, String query, int maxRows,
     * NativePacketPayload packet, boolean streamResults, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory,
     * String catalog, ColumnDefinition cachedMetadata, boolean isBatch)
     *
     * mysql需要使用8.0.17以上的版本,否则或提示找不到方法com.mysql.cj.Query#getCurrentDatabase()
     */
    public static byte[] agentExecute(String args, int slowSqlTime) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(DB_METHOD);
            CtMethod ctMethod = ctClass.getDeclaredMethod("execSQL");
            String methodName = ctMethod.getName();
            String newMethodName = "JavaSsitAop";
            StringBuilder stringBuilder = new StringBuilder();
            CtMethod ctNewMethod = CtNewMethod.copy(ctMethod, ctClass, null);
            //添加字段,慢SQL记录时长
            CtField ctField = CtField.make("int slowSqlTime=" + slowSqlTime + ";", ctClass);
            ctClass.addField(ctField);
            ctNewMethod.setName(methodName + newMethodName);
            ctClass.addMethod(ctNewMethod);

            stringBuilder.append("{long agentStartTime = System.currentTimeMillis();")
                    //Trace记录开始 START
                    //缓存当前线程traceId
                    .append("String traceId = com.evy.common.trace.TraceLogUtils.getCurTraceId();")
                    .append("String database = \"\";")
                    .append("if($1 != null && traceId != null && !\"\".equals(traceId)){")
                    //获取数据库
                    .append("database = ((com.mysql.cj.jdbc.ClientPreparedStatement) $1).getQuery().getCurrentDatabase();")
                    .append("}")
                    //Trace记录开始 END
                    .append("Object result= ").append(ctNewMethod.getName()).append("($$);")
                    .append("long agentEndTime = System.currentTimeMillis() -agentStartTime;")
                    .append("if(null != $1 && agentEndTime >= slowSqlTime){")
                    .append("final String pre = \"com.mysql.cj.jdbc.ClientPreparedStatement: \";")
                    .append("String slowSql = String.valueOf($1);")
                    .append("slowSql = slowSql.replaceAll(pre,\"\");")
                    .append("com.evy.common.trace.TraceUtils.addTraceSql(slowSql,agentEndTime);}")
                    //Trace记录结束 START
                    .append("if($1 != null && traceId != null && !\"\".equals(traceId)){")
                    .append("com.evy.common.trace.TraceLogUtils.setDbTraceId(traceId, database, agentEndTime, agentStartTime);")
                    .append("}")
                    //Trace记录结束 END
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

    public static boolean judge(String className) {
        return DB_METHOD.equals(className);
    }
}
