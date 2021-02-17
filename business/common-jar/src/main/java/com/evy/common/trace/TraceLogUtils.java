package com.evy.common.trace;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.service.TraceTracking;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.SequenceUtils;
import org.springframework.util.StringUtils;

/**
 * Trace Log 工具，用于给当前线程MDC日志赋值TraceId<br/>
 * traceId格式 : traceId[16进制唯一序列]-[int,类型枚举]-[int,顺序自增]
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
     * 构建trace,服务化调用类型
     * @return traceId
     */
    public synchronized static String buildServiceTraceId() {
        return buildTraceId(SERVICE_TYPE);
    }

    /**
     * 构建trace,数据库调用类型
     * @return traceId
     */
    public synchronized static String buildDbTraceId() {
        return buildTraceId(DB_TYPE);
    }

    /**
     * 构建trace,Http调用类型
     * @return traceId
     */
    public synchronized static String buildHttpTraceId() {
        return buildTraceId(HTTP_TYPE);
    }

    /**
     * 从MDC当前线程获取traceId,构建或更新一个新的traceId
     * @param type trace类型
     * @return 更新后的traceId
     */
    private static String buildTraceId(int type) {
        String traceId = CommandLog.getTraceId();
        if (StringUtils.isEmpty(traceId)) {
            //当前线程不存在traceId,新建一个新的traceId
            traceId = buildNewTraceId(type);
        } else {
            traceId = updateTypeByTraceId(traceId, type);
        }

        return traceId;
    }

    /**
     * MDC当前线程赋值TraceId,服务化调用类型
     * @param traceId 唯一序列
     */
    public synchronized static void setServiceTraceId(String traceId) {
        String var = traceId;
        if (StringUtils.isEmpty(var)) {
            var = buildServiceTraceId();
        }
        setLogTraceId(var);
        TraceTracking.saveTraceService(var, APP_NAME);
    }

    /**
     * MDC赋值TraceId,数据库调用类型
     * @param traceId 唯一序列
     */
    public synchronized static void setDbTraceId(String traceId, String databaseName) {
        System.out.println(traceId + "\t" + databaseName);
        String var = traceId;
        if (StringUtils.isEmpty(var)) {
            var = buildDbTraceId();
        }
        setLogTraceId(var);
        TraceTracking.saveTraceDb(var, databaseName);
    }

    /**
     * MDC赋值TraceId,Http调用类型
     * @param traceId 唯一序列
     */
    public synchronized static void setHttpTraceId(String traceId) {
        String var = traceId;
        if (StringUtils.isEmpty(var)) {
            var = buildHttpTraceId();
        }
        setLogTraceId(var);
        TraceTracking.saveTraceHttp(var);
    }

    /**
     * 为当前线程赋值trace或赋值子traceId
     * @param traceId 唯一序列
     */
    private static void setLogTraceId(String traceId) {
        CommandLog.setTraceId(traceId);
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
        setLogTraceId(traceId);
    }

    /**
     * 返回traceId 格式 traceId
     * @param type int,类型枚举
     * @return 返回唯一traceId
     */
    private static String buildNewTraceId(int type) {
        return TRACE_ID + Long.toHexString(SequenceUtils.nextId())
                + BusinessConstant.STRIKE_THROUGH_STR + type
                + BusinessConstant.STRIKE_THROUGH_STR + CommandLog.incr();
    }

    /**
     * 更新traceId中类型type
     * @param traceId traceId
     * @param type int,类型枚举
     * @return 返回更新后traceId
     */
    private static String updateTypeByTraceId(String traceId, int type) {
        int one = traceId.indexOf(BusinessConstant.STRIKE_THROUGH_STR);
        int two = traceId.indexOf(BusinessConstant.STRIKE_THROUGH_STR, one +1);

        int order = Integer.parseInt(traceId.substring(two +1));

        return traceId.substring(0, one +1) + type + BusinessConstant.STRIKE_THROUGH_STR + (order + CommandLog.incr());
    }

    /**
     * 获取当前线程traceId
     * @return traceId
     */
    public synchronized static String getCurTraceId() {
        return CommandLog.getTraceId();
    }
}
