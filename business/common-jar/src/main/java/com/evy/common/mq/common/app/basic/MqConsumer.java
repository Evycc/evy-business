package com.evy.common.mq.common.app.basic;

import com.evy.common.command.infrastructure.config.BusinessProperties;
import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqConsumerModel;
import com.evy.common.mq.rabbitmq.app.RabbitBaseMqConsumer;
import com.evy.common.mq.rabbitmq.app.event.TraceLogEvent;
import com.evy.common.utils.AppContextUtils;
import com.rabbitmq.client.Channel;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * 存储MQ消费者实例集合
     */
    private static final List<BasicMqConsumer> MQ_CONSUMERS = Collections.synchronizedList(new ArrayList<>());
    /**
     * 记录日志流水topic
     */
    public String traceLogTopic;
    /**
     * 记录日志流水tag
     */
    public String traceLogTag;

    public MqConsumer(MqFactory mqFactory, BusinessProperties properties) {
        this.mqFactory = mqFactory;
        traceLogTopic = properties.getTraceLog().getTopic();
        traceLogTag = properties.getTraceLog().getTag();
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
}
