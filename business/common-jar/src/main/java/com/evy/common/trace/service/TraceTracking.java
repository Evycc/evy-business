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
import java.util.Objects;

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
                        String type = vars[1];
                        String srvType = BusinessConstant.ZERO;
                        if (srvType.equals(type)) {
                            String clsName = vars[5];
                            if (Objects.nonNull(clsName)) {
                                String srvName = TraceService.qrySrvName(clsName);
                                if (Objects.nonNull(srvName)) {
                                    //转换类名为对应服务码
                                    traceInfo = traceInfo.replace(clsName, srvName);
                                }
                            }
                        }
                        //链路发生时间戳
                        String order = vars[4];
                        template.opsForZSet()
                                .add(TRACKING_KEY, traceInfo, Double.parseDouble(order))
                                .subscribe();
                    }
            );
            TRACE_LIST.clear();
        }
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳|http请求路径
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param reqPath http请求路径
     * @param takeTime 耗时
     */
    public static void saveHttpTraceId(String traceId, int type, String appName, String reqPath, long takeTime, long timestamp) {
        TRACE_LIST.add(buildLineStr(buildBaseTraceId(traceId, type, appName, takeTime, timestamp), reqPath));
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳|{0发布者|1消费者}|topic|tag
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param isProvider true:发布者 false:消费者
     * @param topic topic
     * @param tag tag
     * @param takeTime 耗时
     * @param timestamp 交易发生时间戳
     */
    public static void saveMqTraceId(String traceId, int type, String appName, boolean isProvider, String topic, String tag, long takeTime, long timestamp) {
        TRACE_LIST.add(buildLineStr(buildBaseTraceId(traceId, type, appName, takeTime, timestamp),
                isProvider ? BusinessConstant.ZERO : BusinessConstant.ONE, topic, tag));
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳|数据库名
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param dataBase 数据库名
     * @param takeTime 耗时
     * @param timestamp 交易发生时间戳
     */
    public static void saveDbTraceId(String traceId, int type, String appName, String dataBase, long takeTime, long timestamp) {
        TRACE_LIST.add(buildLineStr(buildBaseTraceId(traceId, type, appName, takeTime, timestamp), dataBase));
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳|数据库名|表名
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param dataBase 数据库名
     * @param table 表名
     * @param takeTime 耗时
     * @param timestamp 交易发生时间戳
     */
    public static void saveDbTraceId(String traceId, int type, String appName, String dataBase, String table, long takeTime, long timestamp) {
        TRACE_LIST.add(buildLineStr(buildBaseTraceId(traceId, type, appName, takeTime, timestamp), dataBase, table));
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳|服务码
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param srvCode 服务码
     * @param takeTime 耗时
     * @param timestamp 交易发生时间戳
     */
    public static void saveSrvTraceId(String traceId, int type, String appName, String srvCode, long takeTime, long timestamp) {
        TRACE_LIST.add(buildLineStr(buildBaseTraceId(traceId, type, appName, takeTime, timestamp), srvCode));
    }

    /**
     * traceId存储的基本格式: traceId|链路类型|应用名|耗时|时间戳
     * @param traceId traceId
     * @param type 链路类型
     * @param appName 应用名
     * @param takeTime 耗时
     * @return traceId|链路类型|应用名|耗时|时间戳
     * @param timestamp 交易发生时间戳
     */
    private static String buildBaseTraceId(String traceId, int type, String appName, long takeTime, long timestamp) {
        return buildLineStr(traceId, String.valueOf(type), appName, String.valueOf(takeTime), String.valueOf(timestamp));
    }

    /**
     * 用|分割构造字符串
     * @param strs 字符串数组
     * @return 返回格式 str|str
     */
    private static String buildLineStr(String... strs) {
        StringBuilder stringBuilder = new StringBuilder(strs.length);
        for (int i = 0; i < strs.length; i++) {
            stringBuilder.append(strs[i]);
            if (i != strs.length -1) {
                stringBuilder.append(BusinessConstant.LINE);
            }
        }

        return String.valueOf(stringBuilder);
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
