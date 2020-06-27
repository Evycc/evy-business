package com.evy.common.mq.common.infrastructure.tunnel.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MQ消息结构
 * @Author: EvyLiuu
 * @Date: 2019/11/5 22:28
 */
@Builder
@Setter
@ToString
@EqualsAndHashCode
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
     * 消费结束时间
     */
    private String endTime;
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
    private Map<String, String> prpoMap;
    /**
     * 死信队列
     */
    private String dlxQueue;

    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getRbDeliveryTag() {
        return rbDeliveryTag;
    }

    public Object getMessage() {
        return message;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public Map<String, String> getPrpoMap() {
        if (prpoMap != null) {
            return prpoMap;
        }
        prpoMap = new HashMap<>(4);
        return prpoMap;
    }

    public String getDlxQueue() {
        return dlxQueue;
    }
}
