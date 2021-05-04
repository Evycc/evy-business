package com.evy.common.mq.rabbitmq.app.basic;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.mq.rabbitmq.app.RabbitMqSender;
import com.evy.common.mq.rabbitmq.app.event.RabbitMqRetryEvent;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.SerializationUtils;

import java.io.IOException;

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
        long startTime = System.currentTimeMillis();
        try {
            messageAck(envelope.getDeliveryTag());
            CommandLog.info("Start Consumer Service\tTopic:{}\tConsumerID:{}\t", envelope.getExchange(), properties.getCorrelationId());
            MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
            assert sendMessage != null;
            MqSendMessage tmp = MqSendMessage.create();
            tmp.setDlxQueue(sendMessage.getDlxQueue());
            String messageJson = String.valueOf(sendMessage.getMessage());
            CommandLog.info("param:{}", messageJson);

            //执行具体消费逻辑
            int success = execute(consumerTag, envelope, properties, body);
            CommandLog.info("Consumer Result: {}", success);
            switchMqResult(success, sendMessage);

            //清除死信队列
            rabbitMqSender.cleanDlxQueue(tmp);
        } catch (Exception e) {
            CommandLog.errorThrow("消费异常", e);
        }
        CommandLog.info("End Consumer Service({}ms)", System.currentTimeMillis() - startTime);
    }

    /**
     * 处理RabbitMQ消费结果
     *
     * @param result        true : ack对应deliveryTag  false : ack对应deliveryTag，且发送等待消息重试事件
     * @param mqSendMessage 消息体
     */
    private void switchMqResult(int result, MqSendMessage mqSendMessage) {
        if (result == BusinessConstant.FAILED) {
            RabbitMqRetryEvent.waitRetry(mqSendMessage);
        } else {
            RabbitMqRetryEvent.hasRetryKey(mqSendMessage);
        }
    }

    /**
     * 执行消费确认
     * @param deliveryTag ack对应deliveryTag
     */
    private void messageAck(long deliveryTag) {
        try {
            if (MqFactory.AUTO_ACK) {
                Channel channel = getChannel();
                if (channel != null && channel.isOpen()) {
                    CommandLog.info("执行消费确认ack:{}", deliveryTag);
                    getChannel().basicAck(deliveryTag, false);
                }
            }
        } catch (IOException e) {
            CommandLog.errorThrow("获取rabbitmq channel异常 or ack异常", e);
        }
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        doExecute(consumerTag, envelope, properties, body);
    }
}
