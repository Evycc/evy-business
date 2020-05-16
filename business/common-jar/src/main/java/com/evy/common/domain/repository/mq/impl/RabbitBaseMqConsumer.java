package com.evy.common.domain.repository.mq.impl;

import com.evy.common.domain.repository.factory.MqFactory;
import com.evy.common.domain.repository.mq.basic.BaseMqConsumerAdapter;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.DefaultConsumer;
import org.springframework.util.StringUtils;

/**
 * RabbitMQ消费者监听器
 * @Author: EvyLiuu
 * @Date: 2020/1/5 19:33
 */
public class RabbitBaseMqConsumer extends BaseMqConsumerAdapter {

    @Override
    public void consumer() {
        execute((es, m) -> m.forEach((consumer, list) ->
                list.forEach(model -> {
                    String queue = model.getQueue();
                    String tag = model.getTag();
                    es.execute(() -> {
                        CommandLog.info("{} -> 启动监听", consumer);
                        DefaultConsumer defaultConsumer = (DefaultConsumer) consumer;
                        try {
                            if (StringUtils.isEmpty(tag)) {
                                defaultConsumer.getChannel().basicConsume(queue, MqFactory.AUTO_ACK, defaultConsumer);
                            }
                            else {
                                defaultConsumer.getChannel().basicConsume(queue, MqFactory.AUTO_ACK, tag, defaultConsumer);
                            }
                        } catch (Exception e) {
                            CommandLog.errorThrow("RabbitMQ 消费者IO异常", e);
                        }
                    });
                })));
    }

    @Override
    public boolean access(Object bean) {
        return (bean instanceof DefaultConsumer);
    }
}
