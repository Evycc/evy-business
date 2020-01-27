package com.evy.common.domain.repository.mq.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * MQ消息结构
 * @Author: EvyLiuu
 * @Date: 2019/11/5 22:28
 */
@Builder
@Getter
@Setter
@ToString
public class MqSendMessage implements Serializable {
    private static final String serialVersionUID = "qwertyuiopasdfghjklzxcvbnm1234567890";
    /**
     * rabbitmq exchange
     */
    private String topic;
    /**
     * rabbitmq routing_key
     */
    private String tag;
    /**
     * consumer tag
     */
    private String consumerTag;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * messageId
     */
    private String messageId;
    /**
     * rabbitmq long类型消息标签
     */
    private String rbDeliveryTag;
    /**
     * message
     */
    private Object message;
    /**
     * 延时MQ时间
     */
    private long delayTime;
    /**
     * mq 参数
     */
    private Map prpoMap;
    /**
     * 死信队列
     */
    private String dlxQueue;
}
