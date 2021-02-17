package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.utils.AppContextUtils;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 链路跟踪结果记录<br/>
 * 持久化到Redis<br/>
 * key格式 trace:tracking:key<br/>
 * value格式 [traceId]|[备注 应用名或数据库表名]
 * @Author: EvyLiuu
 * @Date: 2020/12/6 0:27
 */
public class TraceTracking {
    private static final String TRACKING_KEY = "trace:tracking:key";
    private static ReactiveRedisTemplate<String, String> template;
    private static final List<String> TRACE_LIST = Collections.synchronizedList(new ArrayList<>(16));

    static {
        init();
    }

    private static void init() {
        ReactiveRedisConnectionFactory factory = AppContextUtils.getBean(ReactiveRedisConnectionFactory.class);
        template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
    }

    /**
     * 执行持久化操作
     */
    public static void execute() {
        if (!CollectionUtils.isEmpty(TRACE_LIST)) {
            TRACE_LIST
                    .forEach(traceInfo -> {
                        String[] vars = traceInfo.split(BusinessConstant.SPLIT_LINE, -1);
                        String traceId = vars[0];
                        //链路执行顺序
                        String order = traceId.substring(traceId.lastIndexOf(BusinessConstant.STRIKE_THROUGH_STR) +1);
                        template.opsForZSet()
                                .add(TRACKING_KEY, traceInfo, Double.parseDouble(order))
                                .subscribe();
                    }
            );
            TRACE_LIST.clear();
        }
    }

    /**
     * Trace记录保存 服务化调用
     * @param traceId   traceId
     * @param appName   应用名
     */
    public static void saveTraceService(String traceId, String appName) {
        TRACE_LIST.add(traceId + BusinessConstant.LINE + appName);
    }

    /**
     * Trace记录保存 数据库操作
     * @param traceId   traceId
     * @param database   表名
     */
    public static void saveTraceDb(String traceId, String database) {
        TRACE_LIST.add(traceId + BusinessConstant.LINE + database);
    }

    /**
     * Trace记录保存 http请求
     * @param traceId traceId
     */
    public static void saveTraceHttp(String traceId) {
        TRACE_LIST.add(traceId);
    }

    /**
     * 查询链路跟踪记录
     * @param traceId traceId
     * @return  排序后链路集合
     */
    public static List<String> searchTraceList(String traceId) {
        List<String> list = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(traceId + "*")
                .build();
        template.opsForZSet()
                .scan(TRACKING_KEY, scanOptions)
                .map(ZSetOperations.TypedTuple::getValue)
                .collectList()
                .blockOptional()
                .ifPresent(list::addAll);

        return list;
    }
}
