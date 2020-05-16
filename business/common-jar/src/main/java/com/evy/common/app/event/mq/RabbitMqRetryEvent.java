package com.evy.common.app.event.mq;

import com.evy.common.domain.repository.mq.impl.RabbitMqSender;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.common.command.utils.JsonUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * RabbitMQ重发MQ事件<br/>
 * 通过设置Redis config : notify-keyspace-events Ex <br/>
 * 监听消息过期信息 subscribe __keyevent@0__:expired <br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/9 0:29
 */
public class RabbitMqRetryEvent {
    private static final String RETRY__KEY = "rabbitmq:retry:";
    private static final String RETRY_EXPIRED_KEY = "rabbitmq:retry:expired:";
    /**
     * 记录等待重发的MQ可重发次数
     */
    private static final String RETRY_COUNT_KEY = "rabbitmq:retry:count:";
    /**
     * 监听redis key过期事件，默认监听16库
     */
    private static final String TOPIC = "__keyevent@0__:expired";
    private static Duration RETRY_TIME;
    private static int RETRY_COUNT;
    private static ReactiveRedisTemplate<String, String> template;
    private static RabbitMqSender rabbitMqSender;

    static {
        init();
        subscribe();
    }

    /**
     * 初始化
     */
    private static void init() {
        ReactiveRedisConnectionFactory factory = AppContextUtils.getBean(ReactiveRedisConnectionFactory.class);
        template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new Jackson2JsonRedisSerializer<>(String.class)));
        rabbitMqSender = AppContextUtils.getBean(RabbitMqSender.class);
        BusinessPrpoties prpoties = AppContextUtils.getPrpo();
        RETRY_TIME = Duration.ofSeconds(prpoties.getMq().getRabbitmq().getConsumerRetryTime());
        RETRY_COUNT = prpoties.getMq().getRabbitmq().getConsumerRetryCount();
    }

    /**
     * 监听过期消息 __keyevent@15__:expired
     */
    private static void subscribe() {
        template.listenTo(ChannelTopic.of(TOPIC))
                .filter(message -> message.getMessage().startsWith(RETRY__KEY))
                .subscribe(message -> template.opsForValue()
                        .increment(RETRY_COUNT_KEY + message.getMessage().substring(RETRY_EXPIRED_KEY.length()))
                        .single()
                        .subscribe(retryCount -> {
                            CommandLog.info("RabbitMQ消息重试次数 : {} 最大次数 : {}", retryCount, RETRY_COUNT);
                            if (retryCount < RETRY_COUNT) {
                                sendRetry(RETRY__KEY + message.getMessage().substring(RETRY_EXPIRED_KEY.length()));
                            }
                        }));
    }

    /**
     * 通过将mq消息序列化，存储到本地MAP，根据RETRY_KEY+序列化组合存储到Redis
     *
     * @param mqSendMessage 等待重发的MQ
     */
    public static void waitRetry(MqSendMessage mqSendMessage) {
        CommandLog.info("waitRetry param: {}", mqSendMessage);
        String code = mqSendMessage.getMessageId();
        String s = JsonUtils.convertToJson(mqSendMessage);
        String retryKey = RETRY__KEY + code;
        String expiredKey = RETRY_EXPIRED_KEY + code;
        String countKey = RETRY_COUNT_KEY + code;

        template.opsForValue().setIfAbsent(retryKey, s).subscribe();
        template.opsForValue().get("")
                .defaultIfEmpty(BusinessConstant.EMPTY_STR)
                .subscribe(value -> {
                    if (!BusinessConstant.EMPTY_STR.equals(value)) {
                        template.opsForValue().increment(countKey).subscribe();
                    }
                });
        template.opsForValue().setIfAbsent(expiredKey, BusinessConstant.EMPTY_STR, RETRY_TIME).subscribe();
    }

    /**
     * 重发MQ
     *
     * @param key RETRY_MAP 存储的KEY
     */
    private static void sendRetry(String key) {
        CommandLog.info("RabbitMQ消息重试 sendRetry param: {}", key);
        template.opsForValue()
                .get(key)
                .subscribe(msg -> {
                    CommandLog.info("sendRetry subscribe: {}", msg);
                    MqSendMessage mqSendMessage = JsonUtils.convertToObject(msg, MqSendMessage.class);
                    CommandLog.info("重发MQ param: {}", mqSendMessage);
                    assert mqSendMessage != null;
                    rabbitMqSender.sendAndConfirm(mqSendMessage);
                }, error -> CommandLog.error("sendRetry异常 {}", error));
    }

    /**
     * 清除重试成功的MQ消息
     *
     * @param mqSendMessage MQ消息体
     */
    public static void clean(MqSendMessage mqSendMessage) {
        CommandLog.info("clean param: {}", mqSendMessage);
        String code = mqSendMessage.getMessageId();
        template.opsForValue().get(RETRY__KEY + code)
                .subscribe(value -> {
                    MqSendMessage result = JsonUtils.convertToObject(value, MqSendMessage.class);
                    assert result != null;
                    if (result.equals(mqSendMessage)) {
                        template.opsForValue()
                                .delete(RETRY_COUNT_KEY + code)
                                .subscribe();
                        template.opsForValue()
                                .delete(RETRY__KEY + code)
                                .subscribe();
                    }
                });

    }

    /**
     * 是否存在消息重试记录
     *
     * @param mqSendMessage MQ消息体
     * @return true 存在
     */
    public static Mono<Boolean> hasRetryKey(MqSendMessage mqSendMessage) {
        CommandLog.info("hasRetryKey param: {}", mqSendMessage);
        return template.hasKey(RETRY_COUNT_KEY + mqSendMessage.getMessageId())
                .defaultIfEmpty(false);
    }
}
