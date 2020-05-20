//package com.evy.linlin.testdemo;
//
//import com.evy.common.domain.repository.mq.anno.AnnoMqConsumer;
//import com.evy.common.domain.repository.mq.basic.BaseRabbitMqConsumer;
//import com.evy.common.domain.repository.mq.model.MqSendMessage;
//import com.evy.common.infrastructure.common.constant.BusinessConstant;
//import com.evy.common.infrastructure.common.log.CommandLog;
//import com.rabbitmq.client.AMQP;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Envelope;
//import org.springframework.util.SerializationUtils;
//
//import java.io.IOException;
//
///**
// * @Author: EvyLiuu
// * @Date: 2020/2/7 22:00
// */
//@AnnoMqConsumer(listen = {@AnnoMqConsumer.AnnoMqConsumerModel(queue = "node2-queue")})
//public class TestNode2 extends BaseRabbitMqConsumer {
//    public TestNode2(Channel channel) {
//        super(channel);
//    }
//
//    @Override
//    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//        CommandLog.info(consumerTag);
//        CommandLog.info("{}", envelope);
//        CommandLog.info("{}", properties);
//        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
//        CommandLog.info(String.valueOf(sendMessage.getMessage()));
//        CommandLog.info("{}", sendMessage);
//
//        return BusinessConstant.SUCESS;
//    }
//
//    @Override
//    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
//        doExecute(consumerTag, envelope, properties, body);
//    }
//}
