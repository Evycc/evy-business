package com.evy.linlin.filter;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.utils.SequenceUtils;
import com.evy.linlin.common.GwErrorCodeConstant;
import com.evy.linlin.filter.infrastructure.tunnel.ServiceInfoModel;
import com.evy.linlin.filter.infrastructure.tunnel.ServiceLimitInfoModel;
import com.evy.linlin.filter.repository.ServiceFilterRepository;
import com.evy.linlin.filter.repository.po.ServiceInfoPO;
import com.evy.linlin.filter.repository.po.ServiceLimitInfoPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 全局过滤器<br/>
 * 网关请求协议:<br/>
 * 服务请求方上送报文必须是POST类型,Body必须包含serviceCode字段<br/>
 * serviceCode : trace_services_info 对应 tsi_service_bean_name字段<br/>
 * serviceCode格式 : 请求服务码 或 请求服务码#方法名
 *
 * @Author: EvyLiuu
 * @Date: 2020/7/18 9:36
 */
@RestController
public class ServiceFilter implements GatewayFilter, Ordered {
    /**
     * 存在服务信息,k : 服务码Bean name  V : 服务信息
     */
    private static final Map<String, ServiceInfoModel> SERVICE_MAP = new HashMap<>(16);
    /**
     * 限流信息临时Map
     */
    private static final Map<String, ServiceLimitInfoModel> SERVICE_LIMIT_INFO_MAP = new HashMap<>(8);
    /**
     * 限流信息临时Map
     */
    private static final Map<String, ServiceLimitInfoModel> SERVICE_LIMIT_INFO_TEMP_MAP = new HashMap<>(8);
    /**
     * 定时调度器
     */
    private static final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1,
            CreateFactory.createThreadFactory(ServiceFilter.class.getName()));
    /**
     * 未找到指定服务,跳转到降级方法
     */
    public static final String SERVICE_NO_FOUND = "/serviceNoFound";
    public static final String GW_ERROR = "/gatewayError";
    public static final String SERVICE_NO_AUTH = "/serviceNoAuth";
    public static final String SERVICE_LIMIT = "/serviceQpsLimit";
    private static final String BODY_HEAD_SERVICE_CODE = "serviceCode";
    private static final OutDTO SERVICE_NOT_FOUND_OUT_DTO = new OutDTO();
    private static final OutDTO GATEWAY_ERROR_OUT_DTO = new OutDTO();
    private static final OutDTO SERVICE_NO_AUTH_OUT_DTO = new OutDTO();
    private static final OutDTO SERVICE_LIMIT_ERR_OUT_DTO = new OutDTO();
    private static final String LOCATION = "Location";
    private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";
    private static final String FILED_TRACE_ID = "traceId";

    @Autowired
    private ServiceFilterRepository repository;

    {
        //未找到服务
        SERVICE_NOT_FOUND_OUT_DTO.setErrorCode(GwErrorCodeConstant.ERROR_SERVICE_NO_FOUND);
        SERVICE_NOT_FOUND_OUT_DTO.setErrorMsg(GwErrorCodeConstant.ERROR_MSG_SERVICE_NO_FOUND);
        //网关异常
        GATEWAY_ERROR_OUT_DTO.setErrorCode(GwErrorCodeConstant.ERROR_SERVICE_GW_ERR);
        GATEWAY_ERROR_OUT_DTO.setErrorMsg(GwErrorCodeConstant.ERROR_MSG_GW_ERR);
        //服务鉴权失败
        SERVICE_NO_AUTH_OUT_DTO.setErrorCode(GwErrorCodeConstant.ERROR_SERVICE_NO_AUTH);
        SERVICE_NO_AUTH_OUT_DTO.setErrorMsg(GwErrorCodeConstant.ERROR_MSG_SERVICE_NO_AUTH);
        //限流
        SERVICE_LIMIT_ERR_OUT_DTO.setErrorCode(GwErrorCodeConstant.ERROR_SERVICE_LIMIT);
        SERVICE_LIMIT_ERR_OUT_DTO.setErrorMsg(GwErrorCodeConstant.ERROR_MSG_SERVICE_LIMIT);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        try {
            //定时从服务器获取限流信息,降级信息

            //取出post请求的body参数
            Map map = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            //取出报文中serviceCode,进行服务鉴权,没有则直接返回错误
            if (!buildServiceNotFoundResponse(response, map)) {
                String srvCode = String.valueOf(map.get(BODY_HEAD_SERVICE_CODE));
                //带方法的服务
                String[] srvAndMethod = srvCode.split(BusinessConstant.SHARE_STR, -1);
                srvCode = srvAndMethod[0];
                String method = srvAndMethod.length > BusinessConstant.ONE_NUM ? srvAndMethod[1] : BusinessConstant.EMPTY_STR;

                //服务限流过滤
                if (!buildLimitResponse(response, srvCode) &&
                        //服务鉴权
                        !buildServiceNoAuthResponse(response, exchange, srvCode)) {
                    //赋值TraceID
                    //TODO 根据traceid 进行链路跟踪
                    buildTraceId(map);
                    //服务路由
                    buildRouteResponse(response, exchange, srvCode, method);
                }
            }
        } catch (Exception exception) {
            CommandLog.errorThrow("ServiceFilter#filter异常", exception);
            buildGwErrorResponse(response);
        }

        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 获取服务限流信息,间隔十分钟
     */
    public void initServiceLimitInfo() {
        executorService.scheduleWithFixedDelay(() -> {
            List<ServiceLimitInfoPO> serviceLimitInfoPoS = repository.queryServiceLimitInfos();
            if (!CollectionUtils.isEmpty(serviceLimitInfoPoS)) {
                //防止并发时限流信息丢失,先保存到临时map
                SERVICE_LIMIT_INFO_TEMP_MAP.putAll(serviceLimitInfoPoS.stream()
                        .collect(Collectors.toMap(ServiceLimitInfoPO::getSliServiceBeanName, ServiceLimitInfoModel::convert)));
                SERVICE_LIMIT_INFO_MAP.putAll(SERVICE_LIMIT_INFO_TEMP_MAP);
            }
        }, 0L, 10L, TimeUnit.MINUTES);
    }

    /**
     * 初始化服务码及对应消费者信息
     */
    public void initServiceInfo() {
        List<ServiceInfoPO> serviceInfoPoS = repository.queryServiceAndConsumers();
        if (!CollectionUtils.isEmpty(serviceInfoPoS)) {
            SERVICE_MAP.putAll(serviceInfoPoS
                    .stream()
                    .collect(Collectors.toMap(ServiceInfoPO::getTsiServiceBeanName, ServiceInfoModel::convert)));
        }
    }

    /**
     * 限流、降级过滤
     *
     * @param response 响应信息
     * @param srvCode  服务码
     * @return true : 进行限流或降级 false : 未配置限流或降级
     */
    private boolean buildLimitResponse(ServerHttpResponse response, String srvCode) {
        ServiceLimitInfoModel infoModel;
        if (SERVICE_LIMIT_INFO_TEMP_MAP.containsKey(srvCode)) {
            infoModel = SERVICE_LIMIT_INFO_TEMP_MAP.get(srvCode);
        } else if (SERVICE_LIMIT_INFO_MAP.containsKey(srvCode)) {
            infoModel = SERVICE_LIMIT_INFO_MAP.get(srvCode);
        } else {
            return false;
        }

        if (infoModel.getServiceQpsLimit() != -1) {
            //存在qps限流
            if (!infoModel.tryExecute()) {
                //存在降级信息
                //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(LOCATION, SERVICE_LIMIT);
                return true;
            }
        } else if (!StringUtils.isEmpty(infoModel.getServiceFallback())) {
            //存在降级信息
            //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(LOCATION, infoModel.getServiceFallback());
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void buildTraceId(Map param) {
        try {
            param.put(FILED_TRACE_ID, SequenceUtils.getNextSeq());
        } catch (Exception exception) {
            CommandLog.error("buildTraceId异常");
        }
    }

    /**
     * 构建正确路由响应
     *
     * @param response 响应信息
     * @param exchange 请求
     * @param srvCode  服务码
     */
    private void buildRouteResponse(ServerHttpResponse response, ServerWebExchange exchange, String srvCode, String method) {
        ServiceInfoModel model = SERVICE_MAP.get(srvCode);
        String forwardPath = BusinessConstant.FORWARD_SLASH_STR.concat(model.getServiceName());

        if (!StringUtils.isEmpty(method)) {
            //服务码存在#,表示路由到指定方法
            String postPaths = model.getPostPath();
            if (postPaths.contains(method)) {
                String methdoPath = Arrays.stream(postPaths.split(BusinessConstant.DOUBLE_LINE, -1))
                        .filter(method::equals)
                        .map(methodPath -> methodPath.split(BusinessConstant.SHARE_STR, -1)[1])
                        .findFirst()
                        .orElse(BusinessConstant.EMPTY_STR);
                if (!StringUtils.isEmpty(methdoPath)) {
                    forwardPath = forwardPath.concat(BusinessConstant.FORWARD_SLASH_STR).concat(methdoPath);
                }
            }
        }

        //设置状态码为307,用于post类型转发,服务调用需要post类型
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        //根据服务码路由到对应服务
        response.getHeaders().set(LOCATION, forwardPath);
        response.getHeaders().setAccessControlRequestMethod(HttpMethod.POST);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8);
        response.writeWith(exchange.getRequest().getBody());
    }

    /**
     * 服务鉴权过滤
     *
     * @param response 响应信息
     * @return false : 服务鉴权通过 true : 服务鉴权失败
     */
    private boolean buildServiceNoAuthResponse(ServerHttpResponse response, ServerWebExchange exchange, String srvCode) {
        String consumerHost = exchange.getRequest().getURI().getHost();

        //服务鉴权
        if (!SERVICE_MAP.get(srvCode).getConsumerHost().contains(consumerHost)) {
            //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(LOCATION, SERVICE_NO_AUTH);
            return true;
        }

        return false;
    }

    /**
     * 构建网关异常响应
     *
     * @param response 响应信息
     */
    private void buildGwErrorResponse(ServerHttpResponse response) {
        //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set(LOCATION, GW_ERROR);
    }

    /**
     * 构建服务不存在响应
     *
     * @param response 响应信息
     * @return true : 服务不存在
     */
    private boolean buildServiceNotFoundResponse(ServerHttpResponse response, Map body) {
        if (Objects.isNull(body)
                || Objects.isNull(body.get(BODY_HEAD_SERVICE_CODE))
                || !SERVICE_MAP.containsKey(String.valueOf(body.get(BODY_HEAD_SERVICE_CODE)))) {
            //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(LOCATION, SERVICE_NO_FOUND);
            return true;
        }
        return false;
    }

    /**
     * 网关过滤异常
     *
     * @return 返回网关错误信息
     */
    @RequestMapping(value = GW_ERROR)
    public Mono<OutDTO> gatewayError() {
        return Mono.just(GATEWAY_ERROR_OUT_DTO);
    }

    /**
     * 服务不存在错误信息
     *
     * @return 返回服务不存在错误信息
     */
    @RequestMapping(value = SERVICE_NO_FOUND)
    public Mono<OutDTO> serviceNoFound() {
        return Mono.just(SERVICE_NOT_FOUND_OUT_DTO);
    }

    /**
     * 服务鉴权失败
     *
     * @return 返回服务鉴权失败
     */
    @RequestMapping(value = SERVICE_NO_AUTH)
    public Mono<OutDTO> serviceNoAuth() {
        return Mono.just(SERVICE_NO_AUTH_OUT_DTO);
    }

    /**
     * 服务鉴权失败
     *
     * @return 返回服务鉴权失败
     */
    @RequestMapping(value = SERVICE_LIMIT)
    public Mono<OutDTO> serviceLimit() {
        return Mono.just(SERVICE_LIMIT_ERR_OUT_DTO);
    }
}
