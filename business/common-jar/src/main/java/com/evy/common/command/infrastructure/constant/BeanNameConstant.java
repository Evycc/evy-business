package com.evy.common.command.infrastructure.constant;

/**
 * Bean名常量类，防止Bean名冲突
 * @Author: EvyLiuu
 * @Date: 2020/12/5 12:05
 */
public class BeanNameConstant {
    /**
     * com.evy.common.command.domain.factory.CreateFactory#httpClient()
     */
    public static final String EVY_HTTP_CLIENT = "EvyHttpClient";
    /**
     * com.evy.common.utils.AppContextUtils
     */
    public static final String APP_CONTEXT_UTILS = "AppContextUtils";
    /**
     * com.evy.common.mq.rabbitmq.app.RabbitMqSender
     */
    public static final String RABBIT_MQ_SENDER = "RabbitMqSender";
}
