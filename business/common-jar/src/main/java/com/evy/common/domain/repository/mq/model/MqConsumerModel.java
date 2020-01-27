package com.evy.common.domain.repository.mq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 存放消费者topic等信息
 * @Author: EvyLiuu
 * @Date: 2019/11/3 17:31
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class MqConsumerModel {
    /**
     * rabbitmq 消费者queue
     */
    @Getter
    @Setter
    private String queue;
    @Getter
    @Setter
    private String tag;
    @Getter
    @Setter
    private String topic;

    public MqConsumerModel(String queue, String tag) {
        this.queue = queue;
        this.tag = tag;
    }
}
