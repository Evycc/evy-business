package com.evy.common.mq.rabbitmq.app.event;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.mq.rabbitmq.app.RabbitMqSender;
import com.evy.common.utils.AppContextUtils;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 通过死信队列,实现RabbitMQ消息重发
 * @Author: EvyLiuu
 * @Date: 2020/6/21 10:42
 */
public class RabbitMqRetryEvent {
    private static final String RETRY_KEY = "rabbitmq:retry:";
    /**
     * 记录等待重发的MQ可重发次数
     */
    private static final String RETRY_COUNT_KEY = "rabbitmq:retry:count:";
    /**
     * 加redis锁,防重消费
     */
    private static final String CONSUMER_LOCK = "rabbitmq:consumer:lock:";
    /**
     * redis锁过期时间,1h
     */
    private static final Duration CONSUMER_LOCK_EXPIRE_TIME = Duration.ofHours(1L);
    private static Duration RETRY_TIME;
    private static int RETRY_COUNT;
    private static RabbitMqSender rabbitMqSender;
    private static ReactiveRedisTemplate<String, String> template;

    static {
        init();
    }

    /**
     * 初始化
     */
    private static void init() {
        rabbitMqSender = AppContextUtils.getBean(RabbitMqSender.class);
        ReactiveRedisConnectionFactory factory = AppContextUtils.getBean(ReactiveRedisConnectionFactory.class);
        template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
        AppContextUtils.getAsyncProp(businessProperties -> {
            RETRY_TIME = Duration.ofSeconds(businessProperties.getMq().getRabbitmq().getConsumerRetryTime());
            RETRY_COUNT = businessProperties.getMq().getRabbitmq().getConsumerRetryCount();

            CommandLog.info("初始化MQ重试次数:{} 重试间隔:{}s", RETRY_COUNT, RETRY_TIME.getSeconds());
        });
    }

    /**
     * 通过将mq消息序列化，存储到本地MAP，根据RETRY_KEY+序列化组合存储到Redis
     *
     * @param mqSendMessage 等待重发的MQ
     */
    public static void waitRetry(MqSendMessage mqSendMessage) {
        CommandLog.info("waitRetry param: {}", mqSendMessage);

        //更改为死信队列
        if (increRetryCount(mqSendMessage)) {
            mqSendMessage.setTopic(getTopicToMqSendMsg(mqSendMessage));
            mqSendMessage.setTag(getTagToMqSendMsg(mqSendMessage));
            sendRetry(mqSendMessage);
        }
    }

    /**
     * 自增消息重试次数
     *
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     * @return 达到重试次数上限返回false
     */
    private static boolean increRetryCount(MqSendMessage mqSendMessage) {
        boolean[] results = new boolean[]{true};

        Optional.ofNullable(mqSendMessage.getPrpoMap().get(RETRY_COUNT_KEY))
                .ifPresentOrElse(count -> {
                    int limit = Integer.parseInt(count) + 1;
                    if (limit >= RETRY_COUNT) {
                        results[0] = false;
                        mqSendMessage.getPrpoMap().remove(RETRY_KEY);
                    }
                    mqSendMessage.getPrpoMap().put(RETRY_COUNT_KEY, String.valueOf(limit));
                }, () -> mqSendMessage.getPrpoMap().put(RETRY_COUNT_KEY, BusinessConstant.ZERO));

        return results[0];
    }

    /**
     * 获取生产者topic,过滤延时MQ生成的topic
     *
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     * @return 获取原topic
     */
    private static String getTopicToMqSendMsg(MqSendMessage mqSendMessage) {
        String srcTopic = mqSendMessage.getTopic();
        if (MqFactory.DLX_EXCHANGE.equals(srcTopic)) {
            srcTopic = mqSendMessage.getPrpoMap().getOrDefault(MqFactory.X_DEAD_LETTER_EXCHANGE, BusinessConstant.EMPTY_STR);
        }
        return srcTopic;
    }

    /**
     * 获取生产者tag,过滤延时MQ生成的tag
     *
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage
     * @return 获取原tag
     */
    private static String getTagToMqSendMsg(MqSendMessage mqSendMessage) {
        String srcTag = mqSendMessage.getTag();
        if (MqFactory.DLX_ROUTING_KEY.equals(srcTag)) {
            srcTag = mqSendMessage.getPrpoMap().getOrDefault(MqFactory.X_DEAD_LETTER_ROUTING_KEY, BusinessConstant.EMPTY_STR);
        }
        return srcTag;
    }

    /**
     * 重发MQ
     */
    private static void sendRetry(MqSendMessage mqSendMessage) {
        CommandLog.info("RabbitMQ消息重试 sendRetry param: {}", mqSendMessage.getMessageId());
        CommandLog.info("重发MQ param: {}", mqSendMessage);
        //标识为消息重试
        if (Optional.ofNullable(mqSendMessage.getPrpoMap().get(RETRY_KEY)).isEmpty()) {
            mqSendMessage.getPrpoMap().put(RETRY_KEY, BusinessConstant.EMPTY_STR);
        }
        rabbitMqSender.sendDelay(mqSendMessage, RETRY_TIME);
    }

    /**
     * 删除存在消息重试标志
     *
     * @param mqSendMessage MQ消息体
     * @return true 清除消息重试标识成功
     */
    public static boolean hasRetryKey(MqSendMessage mqSendMessage) {
        CommandLog.info("hasRetryKey param: {}", mqSendMessage);
        return Optional.ofNullable(mqSendMessage.getPrpoMap().remove(RETRY_KEY)).isPresent();
    }

    /**
     * 消费时给messageId添加锁,只允许单应用消费
     * @param messageId messageId
     * @param consumer  加锁后执行操作
     */
    public static void consumerGetLock(String messageId, Consumer<Boolean> consumer) {
        template.opsForValue()
                .setIfAbsent(CONSUMER_LOCK + messageId, BusinessConstant.ONE, CONSUMER_LOCK_EXPIRE_TIME)
                .blockOptional()
                .ifPresent(consumer);
    }

    /**
     * 消费后给messageId删除锁
     * @param messageId messageId
     */
    public static void consumerCloseLock(String messageId, Consumer<Boolean> consumer) {
        template.opsForValue()
                .delete(CONSUMER_LOCK + messageId)
                .blockOptional()
                .ifPresent(consumer);
    }
}
