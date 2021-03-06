package com.evy.common.trace;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.trace.service.*;
import com.evy.common.utils.AppContextUtils;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 链路跟踪<br/>
 * DB : 统计慢SQL (sql 、 耗时 、 当前IP 、 优化建议) | 配置 : evy.trace.db.flag={0开启|1关闭}<br/>
 * Http :  统计请求总数、耗时、发起方IP、请求地址 | 配置 : evy.trace.http.flag={0开启|1关闭}<br/>
 * redis : 统计redis内存、集群IP、日志地址、健康情况 | 配置 : evy.trace.redis.flag={0开启|1关闭}<br/>
 * MQ : 统计topic请求数、msg耗时、msgID、msgId请求时间 | 配置 : evy.trace.mq.flag={0开启|1关闭}<br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/23 16:49
 */
public class TraceUtils {
    private static final ScheduledThreadPoolExecutor EXECUTOR = CreateFactory.returnScheduledExecutorService("TraceUtils", 8);
    private static int traceServiceTiming = 60;
    private static int traceRedisTiming = 60;
    private static int traceThreadInfoTiming = 60;
    private static int traceMemoryTiming = 60;

    public static void init(){
        //定时监控队列，存在数据则进行处理后入库
        //1分钟后执行
        long initialDelay = 60000L;
        //间隔1分钟轮询
        long delay = 60000L;
        //固定刷新时间
        EXECUTOR.scheduleWithFixedDelay(() -> {
            TraceMqInfo.executeMq();
            TraceHttpInfo.executeHttp();
            TraceSlowSql.executeSlowSql();
            TraceTracking.execute();
        }, initialDelay, delay, TimeUnit.MILLISECONDS);

        //通过配置控制刷新时间
        AppContextUtils.getAsyncProp(businessProperties -> {
            if (Objects.nonNull(businessProperties)) {
                traceMemoryTiming = businessProperties.getTrace().getMemory().getTiming();
                traceServiceTiming = businessProperties.getTrace().getService().getTiming();
                traceThreadInfoTiming = businessProperties.getTrace().getThread().getTiming();
                traceRedisTiming = businessProperties.getTrace().getRedis().getTiming();
            }
            EXECUTOR.scheduleWithFixedDelay(TraceRedisInfo::executeRedisInfo, traceRedisTiming, traceRedisTiming, TimeUnit.SECONDS);
            EXECUTOR.scheduleWithFixedDelay(TraceThreadInfo::executeThreadInfo, traceThreadInfoTiming, traceThreadInfoTiming, TimeUnit.SECONDS);
            EXECUTOR.scheduleWithFixedDelay(TraceAppMemoryInfo::executeMemoryInfo, traceMemoryTiming, traceMemoryTiming, TimeUnit.SECONDS);
            EXECUTOR.scheduleWithFixedDelay(TraceService::executeService, traceServiceTiming, traceServiceTiming, TimeUnit.SECONDS);
        });
    }

    /**
     * 记录慢sql
     *
     * @param slowSql         慢sql
     * @param takeUpTimesatmp 耗时
     */
    public static void addTraceSql(String slowSql, long takeUpTimesatmp) {
        TraceSlowSql.addTraceSql(slowSql, takeUpTimesatmp);
    }

    /**
     * 记录mq生产者信息
     *
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage mq消息体
     */
    public static void addTraceMqSend(MqSendMessage mqSendMessage) {
        TraceMqInfo.addTraceMqSend(mqSendMessage);
    }

    /**
     * 记录mq消费者信息
     *
     * @param msgId           msgID
     * @param takeUpTimestamp 消费结束时间
     */
    public static void addTraceMqEnd(String msgId, long takeUpTimestamp) {
        TraceMqInfo.addTraceMqEnd(msgId, takeUpTimestamp);
    }

    /**
     * 记录http请求耗时
     *
     * @param url             http url
     * @param takeUpTimestamp http响应耗时
     * @param result          http请求结果，true成功，false失败
     * @param inputParam      http请求参数字符串
     * @param respResult      http响应字符串
     */
    public static void addTraceHttp(String url, long takeUpTimestamp, boolean result, String inputParam, String respResult) {
        TraceHttpInfo.addTraceHttp(url, takeUpTimestamp, result, inputParam, respResult);
    }
}