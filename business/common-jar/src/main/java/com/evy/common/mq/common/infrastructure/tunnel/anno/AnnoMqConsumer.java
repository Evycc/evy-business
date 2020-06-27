package com.evy.common.mq.common.infrastructure.tunnel.anno;

import com.evy.common.mq.common.app.basic.BaseMqConsumerAdapter;
import com.evy.common.mq.rabbitmq.app.RabbitBaseMqConsumer;
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
     * @return com.evy.common.mq.common.infrastructure.tunnel.anno.AnnoMqConsumerModel
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
