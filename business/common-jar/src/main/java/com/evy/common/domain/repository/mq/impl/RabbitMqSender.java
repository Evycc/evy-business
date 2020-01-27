package com.evy.common.domain.repository.mq.impl;

import com.evy.common.domain.repository.factory.MqFactory;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.domain.repository.mq.MqSender;
import com.evy.common.infrastructure.common.command.CommandUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.context.AppContextUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ实现
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/3 13:47
 */
@Component("RabbitMqSender")
public class RabbitMqSender implements MqSender {
    private static final ExecutorService EXECUTOR_SERVICE = MqFactory.returnExecutorService("RabbitMqSender-ExecutorService");
    /**
     * 存放需要异步confirm的消息标签MAP
     * K: deliveryTag V: 消息体
     */
    private static SortedMap<Long, MqSendMessage> ACK_MAP = Collections.synchronizedNavigableMap(new TreeMap<>());
    /**
     * 具有addConfirmListener的channel
     */
    private Channel channelConfirm;
    /**
     * 默认初始化不具有监听器的channel
     */
    private Channel channelGeneral;
    private final MqFactory mqFactory;

    public RabbitMqSender(MqFactory mqFactory) {
        this.mqFactory = mqFactory;
    }

    /**
     * 获取rabbitmq channel
     * @return com.rabbitmq.client.Channel
     */
    private Channel getChannel(){
        return mqFactory.getRabbitMqChannel();
    }

    /**
     * 初始化RabbitMQ channel
     * @return com.rabbitmq.client.Channel
     */
    private Channel initChannelGeneral(){
        if (channelGeneral == null || !channelGeneral.isOpen()) {
            channelGeneral = getChannel();
        }

        return channelGeneral;
    }

    /**
     * 初始化RabbitMQ channel
     * @return com.rabbitmq.client.Channel
     */
    private Channel initChannelConfirm() {
        if (channelConfirm == null || !channelConfirm.isOpen()) {
            channelConfirm = getChannel();
            channelConfirm.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long l, boolean b) {
                    //b=true    表示ack了l之前所有的消息
                    if (b) {
                        ACK_MAP.headMap(l - 1).clear();
                    }
                    else {
                        ACK_MAP.remove(l);
                    }
                }

                @Override
                public void handleNack(long l, boolean b) {
                    //未收到ack
                    CommandLog.warn("nack:{}:{} 重新发送消息", l, b);
                    CommandLog.info("未收到ACK，重新发送MQ...");
                    //b=true    表示nack了l之前所有的消息
                    if (b) {
                        SortedMap<Long, MqSendMessage> tempMap = ACK_MAP.headMap(l - 1);
                        if (tempMap != null){
                            tempMap.entrySet().forEach(key -> {
                                MqSendMessage tempMsg = tempMap.get(key);

                                sendAndConfirm(tempMsg, true, initChannelConfirm());
                            });
                        } else {
                            CommandLog.warn("MQ not found Nack deliveryTag: {}", l);
                        }

                        //循环发送MQ完毕，清除之前未ack的deliveryTag
                        ACK_MAP.headMap(l - 1).clear();
                    } else{
                        MqSendMessage tempMsg = ACK_MAP.remove(l);
                        if (tempMsg != null) {
                            sendAndConfirm(tempMsg, true, initChannelConfirm());
                        }
                    }
                }
            });
        }
        return channelConfirm;
    }

    @Override
    public void sendOneWay(String topic, String tag, String consumerTag, Object msg) {
        MqSendMessage mqSendMessage = MqSendMessage.builder()
                .topic(topic)
                .tag(tag)
                .consumerTag(consumerTag)
                .message(msg)
                .build();

        CommandLog.info("Start MQ Send param: {}", mqSendMessage);
        int result = sendAndConfirm(mqSendMessage, false, initChannelGeneral());
        if (result == BusinessConstant.SUCESS) {
            CommandLog.info("MQ发送成功");
        }
        else {
            CommandLog.warn("MQ发送失败");
        }

        CommandLog.info("End MQ Send result: {}", result);
    }

    @Override
    public int sendAndConfirm(String topic, String tag, String consumerTag, Object msg) {
        MqSendMessage mqSendMessage = MqSendMessage.builder()
                .topic(topic)
                .tag(tag)
                .consumerTag(consumerTag)
                .message(msg)
                .build();


        int result = sendAndConfirm(mqSendMessage, true, initChannelConfirm());
        if (result == BusinessConstant.SUCESS) {
            CommandLog.info("MQ发送成功");
        }
        else {
            CommandLog.warn("MQ发送失败");
        }

        return result;
    }

    /**
     * 发送confirm类型的mq
     * @param mqSendMessage com.evy.common.domain.repository.mq.model.MqSendMessage
     * @return  0发送成功   1发送失败
     */
    private int sendAndConfirm(MqSendMessage mqSendMessage, boolean isConfirm, Channel channel){
        CommandLog.info("Start MQ Send Service param: {}", mqSendMessage);
        try {
            String msgId = mqSendMessage.getMessageId();
            String deliveryTag = mqSendMessage.getRbDeliveryTag();

            if (StringUtils.isEmpty(msgId)){
                msgId = String.valueOf(UUID.randomUUID());
                mqSendMessage.setMessageId(msgId);
            }

            if (StringUtils.isEmpty(deliveryTag)){
                deliveryTag = String.valueOf(channel.getNextPublishSeqNo());
                mqSendMessage.setRbDeliveryTag(deliveryTag);
            }

            if (StringUtils.isEmpty(mqSendMessage.getSendTime())){
                mqSendMessage.setSendTime(LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")));
            }

            //MessageProperties
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                    .builder()
                    .contentType("text/plain")
                    .deliveryMode(MqFactory.DELIVERY_MODE)
                    .priority(MqFactory.PRIORITY)
                    .correlationId(msgId)
                    .build();

            if (isConfirm){
                ACK_MAP.put(Long.valueOf(deliveryTag), mqSendMessage);
                channel.confirmSelect();
            }

            CommandLog.info("发送MQ\ttopic:{}\tmessageId:{}", mqSendMessage.getTopic(), msgId);
            channel.basicPublish(mqSendMessage.getTopic(), mqSendMessage.getTag(), basicProperties,
                    SerializationUtils.serialize(mqSendMessage));

            //解绑死信队列
            if (mqSendMessage.getTopic().equals(MqFactory.DLX_EXCHANGE)) {
                channel.queueUnbind(mqSendMessage.getDlxQueue(), MqFactory.DLX_EXCHANGE, MqFactory.DLX_ROUTING_KEY);
            }

            CommandLog.info("End Start MQ Send Service");
            return BusinessConstant.SUCESS;
        } catch (IOException e) {
            CommandLog.errorThrow("{}", e);
            return BusinessConstant.FAILED;
        }
    }

    @Override
    public int sendDelay(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long t) {
        long delayTime = timeUnit.toMillis(t);
        Map<String, Object> map = new HashMap<>(3){{
            put(MqFactory.X_MESSAGE_TTL, delayTime);
            put(MqFactory.X_DEAD_LETTER_EXCHANGE, topic);
            put(MqFactory.X_DEAD_LETTER_ROUTING_KEY, tag);
        }};
        Channel channel = initChannelConfirm();
        try {
            //绑定一个随机名称死信队列
            String dlxqueue = MqFactory.dlxBind(channel, MqFactory.DLX_EXCHANGE, MqFactory.DLX_ROUTING_KEY, map);

            MqSendMessage mqSendMessage = MqSendMessage.builder()
                    .topic(MqFactory.DLX_EXCHANGE)
                    .tag(MqFactory.DLX_ROUTING_KEY)
                    .consumerTag(consumerTag)
                    .message(msg)
                    .delayTime(delayTime)
                    .dlxQueue(dlxqueue)
                    .build();

            sendAndConfirm(mqSendMessage, true, channel);
        } catch (IOException e) {
            CommandLog.errorThrow("发送延时MQ失败", e);
            return BusinessConstant.FAILED;
        }

        return BusinessConstant.SUCESS;
    }

    @Override
    public int sendTiming(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long intervalTime, String startTime, String pattern) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(pattern));
            LocalDateTime now = LocalDateTime.now();

            if (startDate.compareTo(now) < 0) {
                CommandLog.error("Usage: MQ发送时间必须大于等于当前时间，startTime={}", startTime);
                return BusinessConstant.FAILED;
            }

            //定时发送时间
            long st = Duration.between(now, startDate).getSeconds() - intervalTime;

            EXECUTOR_SERVICE.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(st);
                    now.plusSeconds(st);
                    CommandLog.info("定时MQ发送 topic:{} tag: {} 发送MQ时间:{}", topic, tag,
                            now.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));

                    String flag = AppContextUtils.getForEnv(topic + BusinessConstant.POINT + tag);
                    do {
                        sendDelay(topic, tag, consumerTag, msg, timeUnit, intervalTime);
                        TimeUnit.SECONDS.sleep(intervalTime);

                        //定时发送条件开关
                    } while (!StringUtils.isEmpty(flag) && BusinessConstant.ZERO.equals(flag));

                } catch (InterruptedException e) {
                    CommandLog.errorThrow("定时发送MQ异常", e);
                }
            });
        } catch (Exception e) {
            CommandLog.errorThrow("定时发送MQ异常", e);
            return BusinessConstant.FAILED;
        }

        return BusinessConstant.SUCESS;
    }
}