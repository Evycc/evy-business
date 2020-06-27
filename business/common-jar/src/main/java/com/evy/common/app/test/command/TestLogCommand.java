package com.evy.common.app.test.command;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.infrastructure.tunnel.anno.AnnoMqConsumer;
import com.evy.common.mq.common.infrastructure.tunnel.anno.AnnoMqConsumer.AnnoMqConsumerModel;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.mq.rabbitmq.app.basic.BaseRabbitMqConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;

/**
 * test trace log
 * @Author: EvyLiuu
 * @Date: 2019/11/3 16:23
 */
@Component
@AnnoMqConsumer(listen = {@AnnoMqConsumerModel(queue = "queue-command-test")})
public class TestLogCommand extends BaseRabbitMqConsumer {
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

//        if (DateUtils.between(DateUtils.now(),
//                LocalDateTime.parse("20200516 14:50", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
//                .getSeconds() < 0) {
//            return BusinessConstant.SUCESS;
//        }
        return BusinessConstant.FAILED;
    }
}
