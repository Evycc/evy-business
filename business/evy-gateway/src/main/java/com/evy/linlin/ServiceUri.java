package com.evy.linlin;

import com.evy.common.log.CommandLog;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/19 12:23
 */
public class ServiceUri {
    private static final Map<String, String> URI_TEMP_MAP = Collections.synchronizedMap(new HashMap<>(100));
    private static final Map<String, String> SERVICE_MAP = new HashMap<>(16);
    /**
     * 未找到指定服务,跳转到降级方法
     */
    private static final String SERVICE_NO_FOUND = "forward:/s/serviceNoFound";
    private static final String BODY_HEAD_SERVICE_CODE = "serviceCode";
    public static final String BODY_TRACE_ID = "traceId";
    private static final String URI_PREFIX_LB = "lb://";

    /**
     * 保存请求，用于之后的getServiceUri路由到指定服务
     * @param inClass   请求Body
     * @param traceId   唯一序列
     */
    public static void saveServiceBody(Map<String, Object> inClass, String traceId) {
        try {
            CommandLog.info("saveServiceBody start");
            if (Objects.nonNull(inClass)) {
                CommandLog.info("saveServiceBody inClass :{}", inClass);
                String serviceCode = String.valueOf(inClass.get(BODY_HEAD_SERVICE_CODE));
//                if (Objects.nonNull(serviceCode) && SERVICE_MAP.containsKey(serviceCode)) {
//                    // uri : lb://service-name
//                    URI_TEMP_MAP.put(traceId, URI_PREFIX_LB.concat(SERVICE_MAP.get(serviceCode)));
//                }
                if (Objects.nonNull(serviceCode)) {
                    // uri : lb://service-name
                    URI_TEMP_MAP.put(traceId, URI_PREFIX_LB.concat("TEST-DEMO"));
                }
            }
        } catch (Exception e) {
            CommandLog.errorThrow("请求异常", e);
        }
    }

    public static String getServiceUri(PredicateSpec predicateSpec) {
        CommandLog.info("getServiceUri start");
        final String[] var = new String[1];
        String uri = SERVICE_NO_FOUND;
//        String uri = URI_PREFIX_LB + "TEST-DEMO";

//        predicateSpec.asyncPredicate(serverWebExchange -> {
//            var[0] = serverWebExchange.getAttribute(BODY_TRACE_ID);
//            return Mono.just(true);
//        });

//        if (Objects.nonNull(var[0]) && SERVICE_MAP.containsKey(var[0])) {
//            uri = SERVICE_MAP.get(var[0]);
//            SERVICE_MAP.remove(var[0]);
//        }

        if (URI_TEMP_MAP.size() > 0) {
            uri = URI_PREFIX_LB + "TEST-DEMO";
            URI_TEMP_MAP.clear();
        }

        CommandLog.info("getServiceUri end uri : {}", uri);
        return uri;
    }
}
