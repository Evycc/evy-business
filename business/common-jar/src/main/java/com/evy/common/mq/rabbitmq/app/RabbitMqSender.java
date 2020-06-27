package com.evy.common.mq.rabbitmq.app;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.MqSender;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.DateUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService("RabbitMqSender");
    /**
     * 存放需要异步confirm的消息标签MAP
     * K: deliveryTag V: 消息体
     */
    private static final SortedMap<Long, MqSendMessage> ACK_MAP = Collections.synchronizedNavigableMap(new TreeMap<>());
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
                        if (!CollectionUtils.isEmpty(tempMap)){
                            tempMap.entrySet().forEach(key -> {
                                Optional.ofNullable(tempMap.get(key))
                                        .ifPresent(tempMsg -> sendAndConfirm(tempMsg, true, initChannelConfirm()));
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

        sendAndConfirm(mqSendMessage, false, initChannelGeneral());
    }

    @Override
    public int sendAndConfirm(String topic, String tag, String consumerTag, Object msg) {
        MqSendMessage mqSendMessage = MqSendMessage.builder()
                .topic(topic)
                .tag(tag)
                .consumerTag(consumerTag)
                .message(msg)
                .build();

        return sendAndConfirm(mqSendMessage, true, initChannelConfirm());
    }

    /**
     * 发送confirm类型的mq
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     * @return  0发送成功   1发送失败
     */
    public int sendAndConfirm(MqSendMessage mqSendMessage) {
        return sendAndConfirm(mqSendMessage, true, initChannelConfirm());
    }

    /**
     * 发送confirm类型的mq
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     * @return  0发送成功   1发送失败
     */
    private int sendAndConfirm(MqSendMessage mqSendMessage, boolean isConfirm, Channel channel){
        CommandLog.info("Start MQ Send Service param: {}", mqSendMessage);
        try {
            String msgId = mqSendMessage.getMessageId();
            String deliveryTag = mqSendMessage.getRbDeliveryTag();

            if (StringUtils.isEmpty(msgId)){
                msgId = String.valueOf(UUID.randomUUID()).replaceAll(BusinessConstant.STRIKE_THROUGH_STR, BusinessConstant.EMPTY_STR);
                mqSendMessage.setMessageId(msgId);
            }

            if (StringUtils.isEmpty(deliveryTag)){
                deliveryTag = String.valueOf(channel.getNextPublishSeqNo());
                mqSendMessage.setRbDeliveryTag(deliveryTag);
            }

            if (StringUtils.isEmpty(mqSendMessage.getSendTime())){
                mqSendMessage.setSendTime(DateUtils.now().format(DateUtils.PATTERN1));
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
                //开启异步消息确认机制
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
            CommandLog.info("MQ发送成功");

            return BusinessConstant.SUCESS;
        } catch (IOException e) {
            CommandLog.errorThrow("{}", e);
            CommandLog.warn("MQ发送失败");
            return BusinessConstant.FAILED;
        }
    }

    public int sendDelay(MqSendMessage mqSendMessage, Duration timeUnit) {
        long delayTime = timeUnit.toMillis();
        String topic = mqSendMessage.getTopic();
        String tag = mqSendMessage.getTag();
        Channel channel = initChannelConfirm();

        try {
            //绑定一个随机名称死信队列
            String dlxqueue = MqFactory.dlxBind(channel, MqFactory.DLX_EXCHANGE, MqFactory.DLX_ROUTING_KEY, buildDlxMap(topic, tag, delayTime));

            mqSendMessage.getPrpoMap().put(MqFactory.X_DEAD_LETTER_EXCHANGE, topic);
            mqSendMessage.getPrpoMap().put(MqFactory.X_DEAD_LETTER_ROUTING_KEY, tag);
            mqSendMessage.setTopic(MqFactory.DLX_EXCHANGE);
            mqSendMessage.setTag(MqFactory.DLX_ROUTING_KEY);
            mqSendMessage.setDlxQueue(dlxqueue);
            mqSendMessage.setDelayTime(delayTime);

            return sendAndConfirm(mqSendMessage, true, channel);
        } catch (IOException e) {
            CommandLog.errorThrow("发送延时MQ失败", e);
            return BusinessConstant.FAILED;
        }
    }

    @Override
    public int sendDelay(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long t) {
        long delayTime = timeUnit.toMillis(t);
        Channel channel = initChannelConfirm();

        try {
            //绑定一个随机名称死信队列
            String dlxqueue = MqFactory.dlxBind(channel, MqFactory.DLX_EXCHANGE, MqFactory.DLX_ROUTING_KEY, buildDlxMap(topic, tag, delayTime));

            Map<String, String> map1 = new HashMap<>(2);
            map1.put(MqFactory.X_DEAD_LETTER_EXCHANGE, topic);
            map1.put(MqFactory.X_DEAD_LETTER_ROUTING_KEY, tag);
            MqSendMessage mqSendMessage = MqSendMessage.builder()
                    .topic(MqFactory.DLX_EXCHANGE)
                    .tag(MqFactory.DLX_ROUTING_KEY)
                    .consumerTag(consumerTag)
                    .message(msg)
                    .delayTime(delayTime)
                    .dlxQueue(dlxqueue)
                    .prpoMap(map1)
                    .build();

            return sendAndConfirm(mqSendMessage, true, channel);
        } catch (IOException e) {
            CommandLog.errorThrow("发送延时MQ失败", e);
            return BusinessConstant.FAILED;
        }
    }

    private Map<String, Object> buildDlxMap(String topic, String tag, long dlxTime) {
        Map<String, Object> map = new HashMap<>(3);
        map.put(MqFactory.X_MESSAGE_TTL, dlxTime);
        map.put(MqFactory.X_DEAD_LETTER_EXCHANGE, topic);
        map.put(MqFactory.X_DEAD_LETTER_ROUTING_KEY, tag);
        return map;
    }

    /**
     * 定时发起MQ，可通过 topic.tag配置是否停止方法<br/>
     * topic.tag=1 停止定时MQ
     * @param topic 发送的topic（对应rabbitmq中的exchange）
     * @param tag   发送的tag（对应rabbitmq中的routing_key）
     * @param consumerTag      consumer tag
     * @param msg       消息体
     * @param timeUnit  时间单位
     * @param intervalTime  间隔时间
     * @param startTime     启动时间
     * @param pattern       启动时间格式，如yyyyMMdd
     * @return  启动成功返回1
     */
    @Override
    public int sendTiming(String topic, String tag, String consumerTag, Object msg, TimeUnit timeUnit, long intervalTime, String startTime, String pattern) {
        try {
            LocalDateTime startDate = DateUtils.parse(startTime, pattern);
            LocalDateTime now = DateUtils.now();

            if (startDate.compareTo(now) < 0) {
                CommandLog.error("Usage: MQ发送时间必须大于等于当前时间，startTime={}", startTime);
                return BusinessConstant.FAILED;
            }

            //定时发送时间
            long st = DateUtils.between(now, startDate).getSeconds() - intervalTime;

            EXECUTOR_SERVICE.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(st);

                    String flag = AppContextUtils.getForEnv(topic + BusinessConstant.POINT + tag);
                    do {
                        CommandLog.info("定时MQ发送 topic:{} tag: {} 发送MQ时间:{}",
                                topic,
                                tag,
                                DateUtils.plus(now, st, DateUtils.DATE_TYPE.SECONDS, DateUtils.YYYY_MM_DD_HH_MM_SS));
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

    /**
     * 清除死信队列创建的临时队列
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     */
    public void cleanDlxQueue(MqSendMessage mqSendMessage){
        String dlxQueue = mqSendMessage.getDlxQueue();
        if (!StringUtils.isEmpty(dlxQueue)) {
            try {
                //操作队列需要同一channel
                initChannelConfirm().queueDelete(dlxQueue, false, true);
            } catch (IOException e) {
                CommandLog.errorThrow("cleanDlxQueue 清除死信临时队列异常", e);
            }
        }
    }
}
