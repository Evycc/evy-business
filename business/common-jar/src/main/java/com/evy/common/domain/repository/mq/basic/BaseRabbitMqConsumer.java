package com.evy.common.domain.repository.mq.basic;

import com.evy.common.app.event.mq.RabbitMqRetryEvent;
import com.evy.common.domain.repository.mq.impl.RabbitMqSender;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.config.CommandInitialize;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Rabbitmq基础类，封装消费日志打印信息
 *
 * @Author: EvyLiuu
 * @Date: 2020/1/24 12:05
 */
public abstract class BaseRabbitMqConsumer extends DefaultConsumer {
    @Autowired
    @Qualifier("RabbitMqSender")
    private RabbitMqSender rabbitMqSender;

    public BaseRabbitMqConsumer(Channel channel) {
        super(channel);
    }

    /**
     * 具体消费逻辑，由子类实现
     *
     * @param consumerTag 消费者tag
     * @param envelope    存放消费者路由信息
     * @param properties  mq参数
     * @param body        mq参数
     * @return 返回0，执行消息确认ack，反之不执行，等待重新消费
     */
    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        return BusinessConstant.SUCESS;
    }

    /**
     * 实际调用接口
     *
     * @param consumerTag 消费者tag
     * @param envelope    存放消费者路由信息
     * @param properties  mq参数
     * @param body        mq参数
     */
    public void doExecute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        try {
            long statrTime = System.currentTimeMillis();
            CommandLog.info("Start Consumer Service\tTopic:{}\tConsumerID:{}\t", envelope.getExchange(), properties.getCorrelationId());
            MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
            assert sendMessage != null;
            String messageJson = String.valueOf(sendMessage.getMessage());
            CommandLog.info("param:{}", messageJson);

            //执行具体消费逻辑
            int sucess = execute(consumerTag, envelope, properties, body);
            CommandLog.info("Consumer Result: {}", sucess);
            switchMqResult(sucess, sendMessage, envelope.getDeliveryTag());

            CommandLog.info("End Consumer Service({}ms)", System.currentTimeMillis() - statrTime);
        } catch (Exception e) {
            CommandLog.errorThrow("消费异常", e);
        }
    }

    /**
     * 处理RabbitMQ消费结果
     *
     * @param result        true : ack对应deliveryTag  false : ack对应deliveryTag，且发送等待消息重试事件
     * @param mqSendMessage 消息体
     * @param deliveryTag   ack对应deliveryTag
     */
    private void switchMqResult(int result, MqSendMessage mqSendMessage, long deliveryTag) throws IOException {
        Channel channel = getChannel();
        if (channel != null && channel.isOpen()) {
            CommandLog.info("执行消费确认ack:{}", deliveryTag);
            getChannel().basicAck(deliveryTag, false);

            if (result == BusinessConstant.FAILED) {
                RabbitMqRetryEvent.waitRetry(mqSendMessage);
            } else {
                RabbitMqRetryEvent.hasRetryKey(mqSendMessage)
                        .subscribe(hasKey -> {
                            if (hasKey) {
                                CommandLog.info("MQ重试消息消费成功");
                                RabbitMqRetryEvent.clean(mqSendMessage);
                            }
                        });
            }
        }
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        doExecute(consumerTag, envelope, properties, body);
    }
}
