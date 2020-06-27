package com.evy.common.utils;

import com.evy.common.command.infrastructure.config.BusinessPrpoties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 获取Spring ApplicationContext
 * 注意: 使用@SpringBootApplication(scanBasePackages = "com.evy.*")扫描该路径，以注入ApplicationContext
 * @Author: EvyLiuu
 * @Date: 2019/11/9 15:32
 */
@Component
public class AppContextUtils implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;
    private static Environment ENVIRONMENT;
    private static BusinessPrpoties BUSINESS_PRPOTIES;

    public AppContextUtils(final BusinessPrpoties businessPrpoties) {
        BUSINESS_PRPOTIES = businessPrpoties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass){
        return getApplicationContext().getBean(beanClass);
    }

    public static ApplicationContext getApplicationContext(){
        return CONTEXT;
    }

    public static Environment getEnv() {
        return ENVIRONMENT;
    }

    public static String getForEnv(String param){
        if (ENVIRONMENT == null) {
            ENVIRONMENT = getBean(Environment.class);
        }

        return ENVIRONMENT.getProperty(param);
    }

    public static BusinessPrpoties getPrpo(){
        return BUSINESS_PRPOTIES;
    }
}
