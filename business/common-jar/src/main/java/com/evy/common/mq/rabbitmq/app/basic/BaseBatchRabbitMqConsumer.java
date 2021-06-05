package com.evy.common.mq.rabbitmq.app.basic;

import com.evy.common.batch.BatchUtils;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
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
    public void doExecute(Envelope envelope, AMQP.BasicProperties properties, MqSendMessage sendMessage) {
        //批次则更新结束时间
        String batchName = BatchUtils.qryTopicFromBatchName(sendMessage.getTopic());
        if (!StringUtils.isEmpty(batchName)) {
            if (BatchUtils.updateEndDateSql(batchName) == BusinessConstant.FAILED) {
                CommandLog.error("更新批次时间失败");
            }
        }
        super.doExecute(envelope, properties, sendMessage);
    }
}
