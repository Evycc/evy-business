package com.evy.common.domain.repository.mq.basic;

import com.evy.common.domain.repository.mq.model.MqConsumerModel;

import java.util.List;

/**
 * MQ Consumer监听器基类
 * @Author: EvyLiuu
 * @Date: 2020/1/5 19:31
 */
public interface BasicMqConsumer {
    /**
     * 开启MQ监听
     */
    void consumer();

    /**
     * 存储MQ消费者实例及对应topic，tag
     * @param bean  消费者实例
     * @param models    MQ标签集合
     */
    void addConsumer(Object bean, List<MqConsumerModel> models);

    /**
     * 准入条件
     * @param bean  添加的实例对象
     * @return  true:允许添加消费者实例 false:拒绝添加消费者实例
     */
    boolean access(Object bean);
}
