package com.evy.linlin.gateway.common.create;

import com.evy.linlin.gateway.route.ReactiveRedisRouteDefinitionRepository;
import com.evy.linlin.gateway.route.RedisRouteDefinitionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * Bean声明
 * @Author: EvyLiuu
 * @Date: 2020/8/1 10:33
 */
@Component
public class BeanCreater {
    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate(factory, RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
    }

    @Bean
    public RedisRouteDefinitionRepository redisRouteDefinitionRepository(@Qualifier("stringRedisTemplate") StringRedisTemplate redisTemplate) {
        return new RedisRouteDefinitionRepository(redisTemplate);
    }

    @Bean
    public ReactiveRedisRouteDefinitionRepository reactiveRedisRouteDefinitionRepository(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate factory) {
        return new ReactiveRedisRouteDefinitionRepository(factory);
    }
}
