package com.evy.common.infrastructure.common.context;

import com.evy.common.domain.repository.factory.MqFactory;
import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.log.CommandLog;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
    private static final ExecutorService EXECUTOR_SERVICE = MqFactory.returnExecutorService("AppContextUtils-ExecutorService");

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
        if (CONTEXT == null) {
            try {
                return EXECUTOR_SERVICE.submit(() -> {
                    try {
                        int retryCount = 5;
                        while (CONTEXT == null && retryCount-- > 0) {
                            CommandLog.info("等待Spring Context初始化完成,等待时间1s");
                            Thread.sleep(1000L);
                        }
                    } catch (InterruptedException e) {
                        CommandLog.errorThrow("线程等待异常", e);
                    }
                    return CONTEXT;
                }).get();
            } catch (Exception e) {
                CommandLog.errorThrow("等待线程异常", e);
            }
        }
        Objects.requireNonNull(CONTEXT);
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
