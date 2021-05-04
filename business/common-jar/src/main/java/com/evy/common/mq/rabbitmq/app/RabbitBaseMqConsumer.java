package com.evy.common.mq.rabbitmq.app;

import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.BaseMqConsumerAdapter;
import com.evy.common.mq.common.domain.factory.MqFactory;
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
                            //basicConsume 设置autoAck之后,如果再进行手动ack,会导致队列消失
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
