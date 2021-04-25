package com.evy.common.trace;

import com.evy.common.log.CommandLog;
import com.evy.common.trace.service.TraceTracking;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.SequenceUtils;
import org.springframework.util.StringUtils;

/**
 * Trace Log 工具，用于给当前线程MDC日志赋值TraceId
 * @Author: EvyLiuu
 * @Date: 2020/11/30 23:38
 */
public class TraceLogUtils {
    /**
     * traceId
     */
    public static final String TRACE_ID = "traceId";
    /**
     * 服务调用
     */
    private static final int SERVICE_TYPE = 0;
    /**
     * 数据库调用
     */
    private static final int DB_TYPE = 1;
    /**
     * Http调用,非服务化
     */
    private static final int HTTP_TYPE = 2;
    /**
     * MQ调用
     */
    private static final int MQ_TYPE = 3;
    /**
     * 当前应用名
     */
    private static final String APP_NAME = getAppName();

    /**
     * 获取应用名
     *
     * @return 应用名
     */
    private static String getAppName() {
        String appName = "Undefined-App";
        try {
            appName = AppContextUtils.getForEnv("spring.application.name");
        } catch (Exception ignore) {
        }

        return appName;
    }

    /**
     * 从MDC当前线程获取或构建traceId
     * @return traceId
     */
    public static String buildTraceId() {
        String traceId = CommandLog.getTraceId();
        if (StringUtils.isEmpty(traceId)) {
            //当前线程不存在traceId,新建一个新的traceId
            traceId = TRACE_ID + Long.toHexString(SequenceUtils.nextId());
        }

        return traceId;
    }

    /**
     * 删除MDC TraceId
     */
    public synchronized static void rmLogTraceId() {
        CommandLog.rmTraceId();
    }

    /**
     * 更新MDC TraceId
     */
    public synchronized static void rmLogTraceId(String traceId) {
        CommandLog.setTraceId(traceId);
    }

    /**
     * MDC赋值TraceId,http调用类型
     * @param traceId 唯一序列
     * @param takeTimeMs 耗时,ms
     * @param reqPath http请求路径
     * @param timestamp 交易发生时间戳
     */
    public synchronized static void setHttpTraceId(String traceId, String reqPath, long takeTimeMs, long timestamp) {
        TraceTracking.saveHttpTraceId(traceId, HTTP_TYPE, APP_NAME, reqPath, takeTimeMs, timestamp);
    }

    /**
     * MDC赋值TraceId,数据库调用类型
     * @param topic topic
     * @param tag tag
     * @param traceId 唯一序列
     * @param takeTimeMs 耗时,ms
     * @param timestamp 交易发生时间戳
     */
    public synchronized static void setMqTraceId(String traceId, boolean isProvider, String topic, String tag, long takeTimeMs, long timestamp) {
        TraceTracking.saveMqTraceId(traceId, MQ_TYPE, APP_NAME, isProvider, topic, tag, takeTimeMs, timestamp);
    }

    /**
     * MDC赋值TraceId,数据库调用类型
     * @param traceId 唯一序列
     * @param databaseName 数据库名
     * @param takeTimeMs 耗时,ms
     * @param timestamp 交易发生时间戳
     */
    public synchronized static void setDbTraceId(String traceId, String databaseName, long takeTimeMs, long timestamp) {
        TraceTracking.saveDbTraceId(traceId, DB_TYPE, APP_NAME, databaseName, takeTimeMs, timestamp);
    }

    /**
     * MDC赋值TraceId,服务调用类型
     * @param traceId 唯一序列
     * @param srvCode 服务码
     * @param takeTimeMs 耗时,ms
     * @param timestamp 交易发生时间戳
     */
    public synchronized static void setServiceTraceId(String traceId, String srvCode, long takeTimeMs, long timestamp) {
        TraceTracking.saveSrvTraceId(traceId, SERVICE_TYPE, APP_NAME, srvCode, takeTimeMs, timestamp);
    }

    /**
     * 获取当前线程traceId
     * @return traceId
     */
    public synchronized static String getCurTraceId() {
        return CommandLog.getTraceId();
    }
}
