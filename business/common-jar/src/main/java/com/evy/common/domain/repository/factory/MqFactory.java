package com.evy.common.domain.repository.factory;

import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.command.utils.CommandUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * MQ配置类
 * @Author: EvyLiuu
 * @Date: 2019/11/3 22:10
 */
@Component
public class MqFactory {
    @Getter
    private ConnectionFactory rabbitmqConnFactory;
    private String rabbitmqUser;
    private String rabbitmqPassword;
    private String rabbitmqHost;
    private int rabbitmqPort;
    /**
     * 持久化rabbitmq消息
     */
    public static int DELIVERY_MODE;
    /**
     * 消息优先级，默认0
     */
    public static int PRIORITY;
    /**
     * 消息确认
     */
    public static boolean AUTO_ACK;
    /**
     * 自动重连
     */
    public static boolean AUTO_RECOVERY;
    /**
     * 连接重试次数
     */
    private static int CONN_RETRY_COUNT;
    /**
     * rabbitmq最多消费数量，默认不限制
     */
    private static int BASICEQOS;
    /**
     * 死信exchange，用于延时消息推送
     */
    public static final String DLX_EXCHANGE = "topic_dlx_exchange";
    /**
     * 死信routing key，用于延时消息推送
     */
    public static final String DLX_ROUTING_KEY = "rk_dlx";
    /**
     * 消息过期时间，单位ms
     */
    public static final String X_MESSAGE_TTL = "x-message-ttl";
    /**
     * 死信exchange
     */
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    /**
     * 死信routing key
     */
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    public MqFactory(BusinessPrpoties businessPrpoties) {
        rabbitmqUser = businessPrpoties.getMq().getRabbitmq().getUser();
        rabbitmqPassword = businessPrpoties.getMq().getRabbitmq().getPassword();

        if (rabbitmqPassword.startsWith(BusinessConstant.ENC_PRE_STR)) {
            //解密ENC开头的密码
            rabbitmqPassword = CommandUtils.decodeEnc(rabbitmqPassword);
        }

        rabbitmqHost = businessPrpoties.getMq().getRabbitmq().getHost();
        rabbitmqPort = businessPrpoties.getMq().getRabbitmq().getPort();
        DELIVERY_MODE = businessPrpoties.getMq().getRabbitmq().getDeliveryMode();
        PRIORITY = businessPrpoties.getMq().getRabbitmq().getPriority();
        AUTO_ACK = businessPrpoties.getMq().getRabbitmq().isAutoAck();
        AUTO_RECOVERY = businessPrpoties.getMq().getRabbitmq().isAutoRecovery();
        CONN_RETRY_COUNT = businessPrpoties.getMq().getRabbitmq().getConnRetryCount();
        BASICEQOS = businessPrpoties.getMq().getRabbitmq().getBasicQos();
    }

    /**
     * 初始化rabbitmq ConnectionFactory
     */
    private void initConnectionFactory(){
        if (rabbitmqConnFactory == null){
            rabbitmqConnFactory = new ConnectionFactory();
            rabbitmqConnFactory.setUsername(rabbitmqUser);
            rabbitmqConnFactory.setPassword(rabbitmqPassword);
            rabbitmqConnFactory.setHost(rabbitmqHost);
            rabbitmqConnFactory.setPort(rabbitmqPort);
            //TODO RabbitMQ调优
            //启用自动连接恢复
            rabbitmqConnFactory.setAutomaticRecoveryEnabled(AUTO_RECOVERY);
        }
    }

    /**
     * rabbitmq channel
     * @return com.rabbitmq.client.Channel
     */
    @Bean
    public Channel getRabbitMqChannel(){
        if (rabbitmqConnFactory == null) {
            initConnectionFactory();
        }

        Channel channel = null;
        try {
            channel =  rabbitmqConnFactory.newConnection().createChannel();
            if (BASICEQOS != 0) {
                channel.basicQos(BASICEQOS);
            }
            return channel;
        } catch (IOException e) {
            CommandLog.errorThrow("RabbitMQ IO异常", e);
        } catch (TimeoutException e) {
            CommandLog.errorThrow("RabbitMQ connection连接异常", e);
            for (int i = 0; i < CONN_RETRY_COUNT; i++){
                CommandLog.error("尝试重连... {}次", i);

                try {
                    TimeUnit.SECONDS.sleep(1);
                    channel =  rabbitmqConnFactory.newConnection().createChannel();
                    if (BASICEQOS != 0) {
                        channel.basicQos(BASICEQOS);
                    }
                    if (channel != null && channel.isOpen()) {
                        break;
                    }
                } catch (IOException | TimeoutException ex) {
                    CommandLog.error("尝试重连... {}次失败 [errorMsg:{}]", i+1, e.getClass().getName());
                } catch (InterruptedException ex) {
                    CommandLog.errorThrow("尝试重连时异常{}", ex);
                }
            }

        }
        return channel;
    }

    /**
     * 死信队列绑定
     * @param channel   com.rabbitmq.client.Channel
     * @param dlxExchange   dlx_exchange
     * @param dlxRk         dlx_routing_key
     * @param map           带死信参数的map
     * @return              随机名称的queue
     * @throws IOException  绑定queue异常
     */
    public static String dlxBind(Channel channel, String dlxExchange, String dlxRk, Map<String, Object> map) throws IOException {
        String dlxQueue = UUID.randomUUID().toString();
        channel.queueDeclare(dlxQueue, true, true, true, map);
        channel.queueBind(dlxQueue, dlxExchange, dlxRk);

        return dlxQueue;
    }

    /**
     * 队列绑定
     * @param channel   com.rabbitmq.client.Channel
     * @param exchange  exchange
     * @param rk        routing_key
     * @param map   参数
     * @return  随机名称的queue
     * @throws IOException  绑定queue异常
     */
    public static String queueBing(Channel channel, String exchange, String rk, Map<String, Object> map) throws IOException {
        String queue = UUID.randomUUID().toString();
        channel.queueDeclare(queue, true, true, false, map);
        channel.queueBind(queue, exchange, rk);

        return queue;
    }
}
