package com.evy.common.mq.common.app.basic;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqConsumerModel;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * MQ消费者统一监听适配器
 * @Author: EvyLiuu
 * @Date: 2020/1/5 19:55
 */
public abstract class BaseMqConsumerAdapter implements BasicMqConsumer {
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService(BaseMqConsumerAdapter.class.getSimpleName());

    /**
     * 存储MQ消费者实例及对应topic，tag
     */
    private static final Map<Object, List<MqConsumerModel>> CONSUMER_LIST = new ConcurrentHashMap<>();

    @Override
    public void addConsumer(Object bean, List<MqConsumerModel> models) {
        if (access(bean)) {
            CONSUMER_LIST.put(bean, models);
        }
    }

    public void execute(BiConsumer<ExecutorService, Map<Object, List<MqConsumerModel>>> consumer) {
        EXECUTOR_SERVICE.submit(() -> consumer.accept(EXECUTOR_SERVICE, CONSUMER_LIST));
        Runtime.getRuntime().addShutdownHook(new Thread(EXECUTOR_SERVICE::shutdown));
    }

    public Map<Object, List<MqConsumerModel>> getConsumerList() {
        return CONSUMER_LIST;
    }
}
