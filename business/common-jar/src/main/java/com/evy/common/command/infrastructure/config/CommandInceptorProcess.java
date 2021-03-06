package com.evy.common.command.infrastructure.config;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.app.inceptor.BaseCommandInceptor;
import com.evy.common.command.app.inceptor.anno.AnnoCommandInceptor;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.MqConsumer;
import com.evy.common.mq.common.infrastructure.tunnel.anno.AnnoMqConsumer;
import com.evy.common.mq.common.infrastructure.tunnel.anno.AnnoMqConsumer.AnnoMqConsumerModel;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqConsumerModel;
import com.evy.common.trace.service.TraceService;
import com.evy.common.utils.CommandUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean实例化后，初始化CommandInceptor拦截器
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@Component
public class CommandInceptorProcess extends InstantiationAwareBeanPostProcessorAdapter {
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        decodeRedisPass(bean);
        //存在@AnnoCommandInceptor，则添加指定拦截器
        addCommandInceptor(bean);
        //存在@MqConsumer，添加rabbitmq consumer
        addMqConsumer(bean);
        //存在@RestController,添加到服务列表
        TraceService.addControllerCls(beanName, bean);

        return super.postProcessAfterInstantiation(bean, beanName);
    }

    private void decodeRedisPass(Object bean) {
        if (bean.getClass() == LettuceConnectionFactory.class) {
            ((LettuceConnectionFactory) bean).getStandaloneConfiguration()
                    .setPassword(CommandUtils.decodeEnc(((LettuceConnectionFactory) bean).getPassword()));
        }
    }

    /**
     * 添加mq consumer
     * @param bean 添加了 @AnnoMqConsumer 注解的MQ消费类
     * @see AnnoMqConsumer
     */
    private void addMqConsumer(Object bean){
        AnnoMqConsumer mc = bean.getClass().getAnnotation(AnnoMqConsumer.class);
        try {
            if (mc != null){
                AnnoMqConsumerModel[] anncs = mc.listen();
                //com.evy.common.mq.BasicMqConsumer实现类
                Class<?> cls = mc.target();
                List<MqConsumerModel> list = new ArrayList<>();
                for (AnnoMqConsumerModel annc : anncs) {
                    list.add(new MqConsumerModel(annc.queue(), annc.tag()));
                }

                CommandLog.info("添加MqConsumer - {}", bean.getClass().getName());
                MqConsumer.addConsumerBean(cls, bean, list);
            }
        } catch (Exception e) {
            throw new BeanInitializationException("@MqConsumer初始化异常", e);
        }
    }

    /**
     * 添加command拦截器
     * @param bean  bean
     */
    private void addCommandInceptor(Object bean){
        AnnoCommandInceptor aci = bean.getClass().getAnnotation(AnnoCommandInceptor.class);
        try {
            if (aci != null){
                Class<? extends BaseCommandInceptor<?>>[] ins = aci.proxyClass();
                CommandLog.info("添加[{}]拦截器{}",bean.getClass().getName(), ins);

                List<BaseCommandInceptor<?>> inceptors = new ArrayList<>();
                //添加@AnnoCommandInceptor指定的拦截器对象
                for (Class<? extends BaseCommandInceptor<?>> aClass : ins) {
                    BaseCommandInceptor<?> object = aClass.getDeclaredConstructor().newInstance();
                    inceptors.add(object);
                }
                BaseCommandTemplate.addAllInceptor(bean.getClass(), inceptors);
            }
        } catch (Exception e) {
            throw new BeanInitializationException("@AnnoCommandInceptor指定拦截器初始化异常", e);
        }
    }
}
