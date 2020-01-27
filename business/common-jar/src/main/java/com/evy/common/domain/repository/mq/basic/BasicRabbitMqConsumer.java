package com.evy.common.domain.repository.mq.basic;

import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.batch.BatchUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Rabbitmq基础类，封装消费日志打印信息
 * @Author: EvyLiuu
 * @Date: 2020/1/24 12:05
 */
public abstract class BasicRabbitMqConsumer extends DefaultConsumer {
    public BasicRabbitMqConsumer(Channel channel) {
        super(channel);
    }

    /**
     * 具体消费逻辑，由子类实现
     * @param consumerTag   消费者tag
     * @param envelope  存放消费者路由信息
     * @param properties    mq参数
     * @param body  mq参数
     * @return 返回0，执行消息确认ack，反之不执行
     */
    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        return BusinessConstant.SUCESS;
    }

    /**
     * 实际调用接口
     * @param consumerTag   消费者tag
     * @param envelope  存放消费者路由信息
     * @param properties    mq参数
     * @param body  mq参数
     */
    public void doExecute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){
        try {
            long statrTime = System.currentTimeMillis();
            CommandLog.info("Start Consumer Service\tTopic:{}\tConsumerID:{}\t", envelope.getExchange(), properties.getCorrelationId());
            MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
            String messageJson = String.valueOf(sendMessage.getMessage());
            CommandLog.info("param:{}", messageJson);

            //执行具体消费逻辑
            int sucess = execute(consumerTag, envelope, properties, body);
            if (sucess == BusinessConstant.SUCESS) {
                Channel channel = getChannel();
                if (channel != null && channel.isOpen()) {
                    long tag = envelope.getDeliveryTag();
                    CommandLog.info("执行消费确认ack:{}", tag);
                    getChannel().basicAck(tag, false);
                }
            }

            //批次则更新结束时间
            String batchName = BatchUtils.qryTopicFromBatchName(sendMessage.getTopic());
            if (!StringUtils.isEmpty(batchName)) {
                if (BatchUtils.updateEndDateSql(batchName) == BusinessConstant.FAILED) {
                    CommandLog.error("更新批次时间失败");
                }
            }
            CommandLog.info("End Consumer Service({}ms)", System.currentTimeMillis() - statrTime);
        } catch (Exception e) {
            CommandLog.errorThrow("消费异常", e);
        }
    }
}
