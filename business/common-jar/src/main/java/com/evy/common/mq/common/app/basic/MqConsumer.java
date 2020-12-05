package com.evy.common.mq.common.app.basic;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqConsumerModel;
import com.evy.common.mq.rabbitmq.app.RabbitBaseMqConsumer;
import com.evy.common.mq.rabbitmq.app.event.TraceLogEvent;
import com.evy.common.utils.AppContextUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * RabbitMQ 消费者处理
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/3 13:51
 */
@Component
@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
public class MqConsumer {
    /**
     * MQ 参数工厂
     */
    private final MqFactory mqFactory;
    /**
     * 存储RabbitMQ消费者实例及对应topic，tag
     */
    private static final Map<DefaultConsumer, List<MqConsumerModel>> R_LIST = new ConcurrentHashMap<>();
    /**
     * 存储MQ消费者实例集合
     */
    private static final List<BasicMqConsumer> MQ_CONSUMERS = Collections.synchronizedList(new ArrayList<>());
    /**
     * 记录日志流水topic
     */
    @Value("${evy.traceLog.topic:\"\"}")
    public String traceLogTopic;
    /**
     * 记录日志流水tag
     */
    @Value("${evy.traceLog.tag:\"\"}")
    public String traceLogTag;

    public MqConsumer(MqFactory mqFactory) {
        this.mqFactory = mqFactory;
    }

    /**
     * 初始化消费者监听
     */
    @PostConstruct
    private void init() {
        CommandLog.info("初始化MQ Consumer Service..");
        addTraceLogConsumer();
        executeConsumer();
    }

    /**
     * 统一发布消费信息
     */
    private void executeConsumer() {
        MQ_CONSUMERS.forEach(BasicMqConsumer::consumer);
    }

    /**
     * 添加MQ消费者实例
     *
     * @param cls    com.evy.common.mq.BasicMqConsumer实现类
     * @param bean   消费者实例
     * @param models MQ标签集合
     * @param args   实现类构造方法参数
     * @throws NoSuchMethodException     不存在构造方法
     * @throws IllegalAccessException    获取实例失败
     * @throws InvocationTargetException 获取实例失败
     * @throws InstantiationException    获取实例失败
     */
    public static void addConsumerBean(Class<?> cls, Object bean, List<MqConsumerModel> models, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        BasicMqConsumer consumer = MQ_CONSUMERS.stream()
                .filter(basicMqConsumer -> basicMqConsumer.getClass() == cls)
                .findFirst()
                .orElse(args.length > 0 ? (BasicMqConsumer) cls.getDeclaredConstructor().newInstance(args)
                        : (BasicMqConsumer) cls.getDeclaredConstructor().newInstance());
        consumer.addConsumer(bean, models);

        if (!MQ_CONSUMERS.contains(consumer)) {
            MQ_CONSUMERS.add(consumer);
        }
    }

    /**
     * 添加rabbitmq 消费者
     *
     * @param consumer consumer
     * @param models   queue，tag
     * @deprecated com.evy.common.mq.rabbitmq.app.RabbitBaseMqConsumer 通过统一消息监听器进行监听
     */
    @Deprecated
    public static void addRabbitMqConsumer(DefaultConsumer consumer, List<MqConsumerModel> models) {
        R_LIST.put(consumer, models);
    }

    /**
     * 订阅[记录public_log_flow日志流水]
     */
    private void addTraceLogConsumer() {
        try {
            if (StringUtils.isEmpty(traceLogTopic) || StringUtils.isEmpty(traceLogTag)) {
                return;
            }

            TraceLogEvent traceLogEvent = AppContextUtils.getBean(TraceLogEvent.class);
            Channel channel = traceLogEvent.getChannel();
            String queue = MqFactory.dlxBind(channel, traceLogTopic, traceLogTag, null);
            CommandLog.info("创建TraceLog临时消费队列:{}", queue);
            MqConsumerModel mqConsumerModel = new MqConsumerModel();
            mqConsumerModel.setTopic(traceLogTopic);
            mqConsumerModel.setTag(traceLogTag);
            mqConsumerModel.setQueue(queue);
            addConsumerBean(RabbitBaseMqConsumer.class, traceLogEvent, new ArrayList<>() {{
                add(mqConsumerModel);
            }});
        } catch (Exception e) {
            CommandLog.errorThrow("创建TraceLog消费队列异常", e);
        }
    }

    /**
     * 将消费者列表分发到线程监听
     *
     * @deprecated com.evy.common.mq.rabbitmq.app.RabbitBaseMqConsumer 通过统一消息监听器进行监听
     */
    @Deprecated
    public void rabbitmqExecute() {
        ExecutorService es = CreateFactory.returnExecutorService();
        R_LIST.forEach((consumer, list) -> list.forEach(model -> {
            String queue = model.getQueue();
            String tag = model.getTag();
            es.execute(() -> {
                CommandLog.info("{} -> 启动监听", consumer);
                try {
                    if (StringUtils.isEmpty(tag)) {
                        consumer.getChannel().basicConsume(queue, MqFactory.AUTO_ACK, consumer);
                    } else {
                        consumer.getChannel().basicConsume(queue, MqFactory.AUTO_ACK, tag, consumer);
                    }
                } catch (IOException e) {
                    CommandLog.errorThrow("RabbitMQ 消费者IO异常", e);
                }
            });
        }));
    }

}
