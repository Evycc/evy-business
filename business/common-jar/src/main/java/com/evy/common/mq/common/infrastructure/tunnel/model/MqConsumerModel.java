package com.evy.common.mq.common.infrastructure.tunnel.model;

/**
 * 存放消费者topic等信息
 * @Author: EvyLiuu
 * @Date: 2019/11/3 17:31
 */
public class MqConsumerModel {
    /**
     * rabbitmq 消费者queue
     */
    private String queue;
    private String tag;
    private String topic;

    public MqConsumerModel() {
    }

    public MqConsumerModel(String queue, String tag) {
        this.queue = queue;
        this.tag = tag;
    }

    public MqConsumerModel(String queue, String tag, String topic) {
        this.queue = queue;
        this.tag = tag;
        this.topic = topic;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
