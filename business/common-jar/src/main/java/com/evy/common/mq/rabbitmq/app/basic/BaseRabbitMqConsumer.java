package com.evy.common.mq.rabbitmq.app.basic;

import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
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
    @Qualifier(BeanNameConstant.RABBIT_MQ_SENDER)
    private RabbitMqSender rabbitMqSender;

    public BaseRabbitMqConsumer(Channel channel) {
        super(channel);
    }

    /**
     * 具体消费逻辑，由子类实现
     *
     * @param sendMessage mq参数
     * @return 返回0，执行消息确认ack，反之不执行，等待重新消费
     */
    protected int execute(MqSendMessage sendMessage) throws IOException {
        return BusinessConstant.SUCESS;
    }

    /**
     * 实际调用接口
     *
     * @param envelope   存放消费者路由信息
     * @param properties mq参数
     */
    public void doExecute(Envelope envelope, AMQP.BasicProperties properties, MqSendMessage sendMessage) {
        long startTime = System.currentTimeMillis();
        try {
            CommandLog.info("Start Consumer Service\tTopic:{}\tConsumerID:{}\t", envelope.getExchange(), properties.getMessageId());
            MqSendMessage tmp = MqSendMessage.create();
            tmp.setDlxQueue(sendMessage.getDlxQueue());
            String messageJson = String.valueOf(sendMessage.getMessage());
            CommandLog.info("param:{}", messageJson);

            //执行具体消费逻辑
            int success = execute(sendMessage);
            CommandLog.info("Consumer Result: {}", success);
            switchMqResult(success, sendMessage, envelope.getDeliveryTag());

            //清除死信队列
            rabbitMqSender.cleanDlxQueue(tmp);
        } catch (Exception e) {
            CommandLog.errorThrow("消费异常", e);
        }
        CommandLog.info("End Consumer Service({}ms)", System.currentTimeMillis() - startTime);
    }

    /**
     * 执行消费确认
     *
     * @param deliveryTag ack对应deliveryTag
     * @param result      true: 执行ack false: 放回队列重新消费
     */
    public void messageAck(long deliveryTag, boolean result) {
        try {
            Channel channel = getChannel();
            if (channel != null && channel.isOpen()) {
                CommandLog.info("执行消费确认ack:{}", deliveryTag);
                if (result) {
                    getChannel().basicAck(deliveryTag, false);
                } else {
                    getChannel().basicNack(deliveryTag, false, true);
                }
            }
        } catch (IOException e) {
            CommandLog.errorThrow("获取rabbitmq channel异常 or ack异常", e);
        }
    }

    /**
     * 处理RabbitMQ消费结果
     *
     * @param result        true : ack对应deliveryTag  false : ack对应deliveryTag，且发送等待消息重试事件
     * @param mqSendMessage 消息体
     */
    private void switchMqResult(int result, MqSendMessage mqSendMessage, long ackTag) {
        if (result == BusinessConstant.FAILED) {
            RabbitMqRetryEvent.waitRetry(mqSendMessage);
            //等待重新消费，对msgId释放锁
            RabbitMqRetryEvent.consumerCloseLock(mqSendMessage.getMessageId(),
                    var ->
                    //用死信队列进行重复消费，不进行nack
                    messageAck(ackTag, true)
                    //messageAck(ackTag, false)
            );
        } else {
            RabbitMqRetryEvent.hasRetryKey(mqSendMessage);
            messageAck(ackTag, true);
        }
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
        assert sendMessage != null;
        if (!sendMessage.isFanout()) {
            //非广播消费加锁防止多个应用同时消费
            RabbitMqRetryEvent.consumerGetLock(sendMessage.getMessageId(),
                    result -> {
                        if (result) {
                            doExecute(envelope, properties, sendMessage);
                        } else {
                            messageAck(envelope.getDeliveryTag(), true);
                        }
                    });
        } else {
            doExecute(envelope, properties, sendMessage);
        }
    }
}
