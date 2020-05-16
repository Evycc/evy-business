package com.evy.common.domain.repository.factory;

import com.evy.common.infrastructure.common.constant.BusinessConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;

/**
 * 声明Bean或创建实例类工厂
 * @Author: EvyLiuu
 * @Date: 2020/5/7 23:45
 */
@Component
public class CreateFactory {
    /**
     * 返回基于CPU核心数的线程池
     * @return java.util.concurrent.ExecutorService
     */
    @Deprecated
    public static ExecutorService returnExecutorService(){
        return returnExecutorService(null);
    }

    /**
     * 返回基于CPU核心数的线程池
     * @param name 线程池名称
     * @return java.util.concurrent.ExecutorService
     */
    public static ExecutorService returnExecutorService(String name){
        ExecutorService es = new ThreadPoolExecutor(
                //核心线程数
                BusinessConstant.CORE_CPU_COUNT + 1,
                //最大线程数 核心数*2+1
                (BusinessConstant.CORE_CPU_COUNT << 1) + 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(1000),
                createThreadFactory(name),
                //拒绝策略  抛出RejectedExecutionException
                new ThreadPoolExecutor.AbortPolicy());

        //停止线程，不可submit，等待已执行的线程
        Runtime.getRuntime().addShutdownHook(new Thread(es::shutdown));
        return es;
    }

    /**
     * ThreadFactory
     * @param name  ThreadFactory名称
     * @return java.util.concurrent.ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String name) {
        if (StringUtils.isEmpty(name)) {
            return Executors.defaultThreadFactory();
        }
        //参照com.google.common.util.concurrent.ThreadFactoryBuilder.build(com.google.common.util.concurrent.ThreadFactoryBuilder)
        return runnable -> {
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
            Thread thread = defaultThreadFactory.newThread(runnable);
            //TODO 构建唯一值
            thread.setName(name);
            return thread;
        };
    }
}
