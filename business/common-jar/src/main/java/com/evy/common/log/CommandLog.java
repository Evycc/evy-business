package com.evy.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志通用工具
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/26 16:12
 */
public class CommandLog {
    /**
     * 存储实例对象的looger实例 Map <br>
     * K:类名 V:logger实例
     */
    private final static Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>();
    /**
     * 0:info
     */
    private final static int INFO_ZERO = 0;
    /**
     * 1:debug
     */
    private final static int DEBUG_ONE = 1;
    /**
     * 2:warn
     */
    private final static int WARN_TWO = 2;
    /**
     * 3:tarce
     */
    private final static int TRACE_THREE = 3;
    /**
     * 4:error
     */
    private final static int ERROE_FOUR = 4;

    /**
     * 获取需要打印log对应logger类
     *
     * @return logger
     */
    private static Logger getInvokeLogger() {
        //调用链路为 logger类->printLog()->getInvokeLogger()
        //所以getStackTrace()[2]获取实际logger类
        String cln = Thread.currentThread().getStackTrace()[2].getClassName();

        return getLoggerByClassName(cln);
    }

    public static void info(String msg, Object... obj) {
        printLog(msg, getInvokeLogger(), INFO_ZERO, obj);
    }

    public static void debug(String msg, Object... obj) {
        printLog(msg, getInvokeLogger(), DEBUG_ONE, obj);
    }

    public static void warn(String msg, Object... obj) {
        printLog(msg, getInvokeLogger(), WARN_TWO, obj);
    }

    public static void trace(String msg, Object... obj) {
        printLog(msg, getInvokeLogger(), TRACE_THREE, obj);
    }

    public static void error(String msg, Object... obj) {
        printLog(msg, getInvokeLogger(), ERROE_FOUR, obj);
    }

    public static void info(String msg) {
        printLog(msg, getInvokeLogger(), INFO_ZERO);
    }

    public static void debug(String msg) {
        printLog(msg, getInvokeLogger(), DEBUG_ONE);
    }

    public static void warn(String msg) {
        printLog(msg, getInvokeLogger(), WARN_TWO);
    }

    public static void trace(String msg) {
        printLog(msg, getInvokeLogger(), TRACE_THREE);
    }

    public static void error(String msg) {
        printLog(msg, getInvokeLogger(), ERROE_FOUR);
    }

    private static final String TRACE_ID = "traceId";
    private static final String INCR_INT = "incrInt";

    /**
     * 线程内自增整形
     * @return int
     */
    public static int incr() {
        String incr = MDC.get(INCR_INT);
        int intIncr;
        if (StringUtils.isEmpty(incr)) {
            intIncr = 0;
        } else {
            intIncr = Integer.parseInt(incr);
            ++intIncr;
        }
        setMdc(INCR_INT, String.valueOf(intIncr));
        return intIncr;
    }

    /**
     * 线程结束，重置自增变量
     */
    public static void rmIncr() {
        rmMdc(INCR_INT);
    }

    /**
     * 存储到MDC，用于logger
     *
     * @param traceId %X{traceId}
     */
    public static void setTraceId(String traceId) {
        setMdc(TRACE_ID, traceId);
    }

    /**
     * 获取MDC traceId
     *
     * @return traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 存储到MDC，用于logger
     *
     * @param key   MDC key 用法pattern : %X{key}
     * @param value MDC value
     */
    public static void setMdc(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * 删除MDC中 traceId
     */
    public static void rmTraceId() {
        rmMdc(TRACE_ID);
        rmIncr();
    }

    /**
     * 删除MDC Key
     *
     * @param key MDC key
     */
    public static void rmMdc(String key) {
        MDC.remove(key);
    }

    /**
     * 获取对应logger对象
     *
     * @param cln 类名
     * @return 返回Logger对象
     */
    private static Logger getLoggerByClassName(String cln) {
        Logger logger;
        if (LOGGER_MAP.isEmpty() || !LOGGER_MAP.containsKey(cln)) {
            logger = LoggerFactory.getLogger(cln);
            LOGGER_MAP.put(cln, logger);
        } else {
            logger = LOGGER_MAP.get(cln);
        }
        return logger;
    }

    /**
     * 打印异常堆栈
     *
     * @param msg msg
     * @param t   exception
     */
    public static void errorThrow(String msg, Throwable t) {
        Logger logger = getInvokeLogger();
        if (logger.isErrorEnabled()) {
            logger.error(msg, t);
        }
    }

    /**
     * 打印日志
     *
     * @param msg    内容
     * @param logger 日志实现类
     * @param level  日志等级 0:info 1:debug 2:warn 3:tarce 4:error
     * @param obj    {} 对象
     */
    private static void printLog(String msg, Logger logger, int level, Object... obj) {
        switch (level) {
            case 0:
                if (logger.isInfoEnabled()) {
                    logger.info(msg, obj);
                }
                break;
            case 1:
                if (logger.isDebugEnabled()) {
                    logger.debug(msg, obj);
                }
                break;
            case 2:
                if (logger.isWarnEnabled()) {
                    logger.warn(msg, obj);
                }
                break;
            case 3:
                if (logger.isTraceEnabled()) {
                    logger.trace(msg, obj);
                }
                break;
            case 4:
                if (logger.isErrorEnabled()) {
                    logger.error(msg, obj);
                }
                break;
            default:
                break;
        }
    }
}
