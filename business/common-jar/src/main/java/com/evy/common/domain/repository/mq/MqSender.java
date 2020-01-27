package com.evy.common.domain.repository.mq;

import java.util.concurrent.TimeUnit;

/**
 * MQ发送者
 * @Author: EvyLiuu
 * @Date: 2019/11/1 22:36
 */
public interface MqSender {
    /**
     * 单向消息
     * @param topic 发送的topic（对应rabbitmq中的exchange）
     * @param tag   发送的tag（对应rabbitmq中的routing_key）
     * @param consumerTag  consumer tag
     * @param msg   消息体
     */
    void sendOneWay(String topic, String tag, String consumerTag, Object msg);

    /**
     * 双向消息
     * @param topic 发送的topic（对应rabbitmq中的exchange）
     * @param tag   发送的tag（对应rabbitmq中的routing_key）
     * @param consumerTag  consumer tag
     * @param msg   消息体
     * @return  0：成功  1：失败
     */
    int sendAndConfirm(String topic, String tag, String consumerTag, Object msg);

    /**
     * 延时发送消息
     * @param topic 发送的topic（对应rabbitmq中的exchange）
     * @param tag   发送的tag（对应rabbitmq中的routing_key）
     * @param consumerTag  consumer tag
     * @param msg   消息体
     * @param timeUnit  时间单位
     * @param t 时间
     * @return  0：成功  1：失败
     */
    int sendDelay(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long t);

    /**
     * 定时发送消息，定时发送条件为 [topic.tag=0]
     * @param topic 发送的topic（对应rabbitmq中的exchange）
     * @param tag   发送的tag（对应rabbitmq中的routing_key）
     * @param consumerTag      consumer tag
     * @param msg       消息体
     * @param timeUnit  时间单位
     * @param intervalTime  间隔时间
     * @param startTime     启动时间
     * @param pattern       启动时间格式，如yyyyMMdd
     * @return  0：成功  1：失败
     */
    int sendTiming(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long intervalTime,
                   String startTime, String pattern);
}
