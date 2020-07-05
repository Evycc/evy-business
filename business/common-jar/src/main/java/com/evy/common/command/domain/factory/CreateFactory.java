package com.evy.common.command.domain.factory;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;
import com.evy.common.utils.SequenceUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static ThreadFactory createThreadFactory(String name) {
        //参照com.google.common.util.concurrent.ThreadFactoryBuilder.build(com.google.common.util.concurrent.ThreadFactoryBuilder)
        return runnable -> {
            ThreadFactory defaultThreadFactory = StringUtils.isEmpty(name) ?
                    new DefaultThreadFactory() : new DefaultThreadFactory(name);
            return defaultThreadFactory.newThread(runnable);
        };
    }

    /**
     * 参照java.util.concurrent.Executors.DefaultThreadFactory
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        /**
         * 1 : 自定义线程名   0 : 唯一序列
         */
        private final int flag;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = Long.toHexString(SequenceUtils.nextId());
            flag = 0;
        }

        DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name + BusinessConstant.STRIKE_THROUGH_STR +
                    poolNumber.getAndIncrement() + BusinessConstant.STRIKE_THROUGH_STR;
            flag = 1;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    flag == 1 ? namePrefix + threadNumber.getAndIncrement()
                    : Long.toHexString(SequenceUtils.nextId()),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * org.springframework.data.redis.connection.RedisConnectionFactory
     * @return 获取org.springframework.data.redis.connection.RedisConnectionFactory 或 null
     */
    public static RedisConnectionFactory returnRedisConnFactory() {
        RedisConnectionFactory redisConnectionFactory = null;
        try {
            redisConnectionFactory = AppContextUtils.getBean(RedisConnectionFactory.class);
        } catch (Exception ignore) {
        }
        return redisConnectionFactory;
    }

    /**
     * 返回RedisConnection
     * @param host host
     * @param port port
     * @param pass 密码或env加密密码
     * @return org.springframework.data.redis.connection.lettuce.LettuceConnection  获取连接异常时返回Null
     */
    public static LettuceConnection returnRedisConn(String host, int port, String pass) {
        LettuceConnection conn = null;
        long timeOut = 10000L;
        //等待3s秒连接redis
        long waitPing = 100L;
        int waitCount = 10;
        String pong = "PONG";

        try {
            conn = new LettuceConnection(timeOut, returnRedisClient(host, port, pass));
            while (true) {
                try {
                    if (pong.equalsIgnoreCase(conn.ping())) {
                        break;
                    }
                } catch (Exception exception) {
                    if (waitCount-- <= BusinessConstant.SUCESS) {
                        conn = null;
                        break;
                    }
                    CommandLog.info("尝试连接redis host :{} port :{} 剩余次数:{}", host, port, waitCount);
                    TimeUnit.MILLISECONDS.sleep(waitPing);
                }
            }
        } catch (Exception e) {
            CommandLog.errorThrow("returnRedisConn异常", e);
        }
        return conn;
    }

    /**
     * 构建io.lettuce.core.RedisClient
     * @param host host
     * @param port port
     * @param pass 密码或env加密密码
     * @return  io.lettuce.core.RedisClient
     */
    public static RedisClient returnRedisClient(String host, int port, String pass) {
        RedisURI.Builder builder = RedisURI.builder().withHost(host).withPort(port);
        if (!StringUtils.isEmpty(pass)) {
            builder.withPassword(CommandUtils.decodeEnc(pass));
        }
        return RedisClient.create(builder.build());
    }
}
