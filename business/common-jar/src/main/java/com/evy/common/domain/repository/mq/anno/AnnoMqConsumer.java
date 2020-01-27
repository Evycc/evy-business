package com.evy.common.domain.repository.mq.anno;

import com.evy.common.domain.repository.mq.basic.BaseMqConsumerAdapter;
import com.evy.common.domain.repository.mq.impl.RabbitBaseMqConsumer;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * MQ消费者
 * @Author: EvyLiuu
 * @Date: 2019/11/3 13:40
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AnnoMqConsumer {
    /**
     * 消费者模型
     * @return com.evy.common.domain.repository.mq.anno.AnnoMqConsumerModel
     */
    AnnoMqConsumerModel[] listen() default {};

    Class<? extends BaseMqConsumerAdapter> target() default RabbitBaseMqConsumer.class;

    /**
     * MQ Consumer模型
     * @Author: EvyLiuu
     * @Date: 2019/11/3 16:38
     */
    @Target({})
    public @interface AnnoMqConsumerModel {
        /**
         * rabbitmq 消费者队列
         * @return  queue
         */
        String queue() default "";

        /**
         * 消费者tag
         * @return consumer tag
         */
        String tag() default "";
    }
}
