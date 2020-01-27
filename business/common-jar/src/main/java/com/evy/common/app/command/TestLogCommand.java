package com.evy.common.app.command;

import com.evy.common.domain.repository.mq.basic.BasicRabbitMqConsumer;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.domain.repository.mq.anno.AnnoMqConsumer;
import com.evy.common.domain.repository.mq.anno.AnnoMqConsumer.AnnoMqConsumerModel;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.SneakyThrows;
import org.springframework.util.SerializationUtils;

import java.io.IOException;

/**
 * test trace log
 * @Author: EvyLiuu
 * @Date: 2019/11/3 16:23
 */
@AnnoMqConsumer(listen = {@AnnoMqConsumerModel(queue = "queue-command-test")})
public class TestLogCommand extends BasicRabbitMqConsumer {
    public TestLogCommand(Channel channel) {
        super(channel);
    }

    @Override
    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        CommandLog.info(consumerTag);
        CommandLog.info("{}", envelope);
        CommandLog.info("{}", properties);
        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
        CommandLog.info(String.valueOf(sendMessage.getMessage()));
        CommandLog.info("{}", sendMessage);

        return BusinessConstant.SUCESS;
    }

    @SneakyThrows
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        doExecute(consumerTag, envelope, properties, body);
    }
}
