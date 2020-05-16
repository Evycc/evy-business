package com.evy.common.domain.repository.mq.basic;

import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.batch.BatchUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

/**
 * 批次消费者RabbitMQ实例
 * @Author: EvyLiuu
 * @Date: 2020/5/7 22:14
 */
public class BaseBatchRabbitMqConsumer extends BaseRabbitMqConsumer {
    public BaseBatchRabbitMqConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void doExecute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
        //批次则更新结束时间
        String batchName = BatchUtils.qryTopicFromBatchName(sendMessage.getTopic());
        if (!StringUtils.isEmpty(batchName)) {
            if (BatchUtils.updateEndDateSql(batchName) == BusinessConstant.FAILED) {
                CommandLog.error("更新批次时间失败");
            }
        }
        super.doExecute(consumerTag, envelope, properties, body);
    }
}
