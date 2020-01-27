package com.evy.common.domain.repository.mq.basic;

import com.evy.common.domain.repository.factory.MqFactory;
import com.evy.common.domain.repository.mq.model.MqConsumerModel;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * MQ消费者监听适配器
 * @Author: EvyLiuu
 * @Date: 2020/1/5 19:55
 */
public abstract class BaseMqConsumerAdapter implements BasicMqConsumer {
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
        ExecutorService es = MqFactory.returnExecutorService();
        es.submit(() -> consumer.accept(es, CONSUMER_LIST));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> es.shutdown()));
    }

    public Map<Object, List<MqConsumerModel>> getConsumerList() {
        return CONSUMER_LIST;
    }
}
