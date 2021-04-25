package com.evy.linlin.gateway.filter;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;
import com.evy.linlin.gateway.common.GwErrorCodeConstant;
import com.evy.linlin.gateway.filter.repository.ServiceFilterRepository;
import com.evy.linlin.gateway.filter.repository.po.ServiceInfoPO;
import com.evy.linlin.gateway.filter.repository.po.ServiceLimitInfoPO;
import com.evy.linlin.gateway.filter.tunnel.ServiceInfoModel;
import com.evy.linlin.gateway.filter.tunnel.ServiceLimitInfoModel;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
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
    private final ServiceFilterRepository repository;
    /**
     * 存在服务信息,k : 服务码Bean name  V : 服务信息
     */
    private static final Map<String, ServiceInfoModel> SERVICE_MAP = new HashMap<>(16);
    /**
     * 临时服务列表
     */
    private static final Map<String, ServiceInfoModel> SERVICE_TEMP_MAP = new HashMap<>(16);
    /**
     * 缓存服务集合，用于对比数据库是否存在变化
     */
    private static final List<ServiceInfoPO> CACHE_SERVICE_LIST = new ArrayList<>();
    /**
     * 限流信息Map
     */
    private static final Map<String, ServiceLimitInfoModel> SERVICE_LIMIT_INFO_MAP = new HashMap<>(8);
    /**
     * 限流信息临时Map
     */
    private static final Map<String, ServiceLimitInfoModel> SERVICE_LIMIT_INFO_TEMP_MAP = new HashMap<>(8);
    /**
     * 缓存限流信息集合，用于对比数据库是否存在变化
     */
    private static final List<ServiceLimitInfoPO> CACHE_SERVICE_LIMIT_LIST = new ArrayList<>();
    /**
     * 定时调度器
     */
    private static final ScheduledThreadPoolExecutor executorService = CreateFactory.returnScheduledExecutorService(ServiceFilter.class.getName(), 2);

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
    /**
     * IPV6格式
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile("^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:)|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){1,6})|(:(:[0-9A-Fa-f]{1,4}){1,7})|(([0-9A-Fa-f]{1,4}:){6}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){0,4}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(:(:[0-9A-Fa-f]{1,4}){0,5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}))$");
    /**
     * IPV4格式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$");

    static {
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

    public ServiceFilter(ServiceFilterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        try {
            //取出post请求的body参数
            //已在过滤器进行参数过滤，参数为空返回404
            Map<String, String> map = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            CommandLog.info("服务码化路由 url:{} body:{}", exchange.getRequest().getURI(), map);
            String srvCode = String.valueOf(map.get(BODY_HEAD_SERVICE_CODE));
            //带方法的服务
            //支持通过srvCode : 服务码#req_path的方式进行调用
            String[] srvAndMethod = srvCode.split(BusinessConstant.SHARE_STR, -1);
            srvCode = srvAndMethod[0];

            //取出报文中serviceCode,进行服务鉴权,没有则直接返回错误
            if (!CollectionUtils.isEmpty(map) && !buildServiceNotFoundResponse(response, srvCode)) {
                String method = srvAndMethod.length > BusinessConstant.ONE_NUM ? srvAndMethod[1] : BusinessConstant.EMPTY_STR;

                //服务限流过滤
                if (!buildLimitResponse(response, srvCode) &&
                        //服务鉴权
                        !buildServiceNoAuthResponse(response, exchange, srvCode)) {
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
            try {
                CommandLog.info("获取服务限流降级信息");
                List<ServiceLimitInfoPO> serviceLimitInfoPoS = repository.queryServiceLimitInfos();
                if (!CollectionUtils.isEmpty(serviceLimitInfoPoS)
                        || serviceLimitInfoPoS.hashCode() != CACHE_SERVICE_LIMIT_LIST.hashCode()
                        || serviceLimitInfoPoS.size() != CACHE_SERVICE_LIMIT_LIST.size()) {
                    //保存缓存记录
                    CACHE_SERVICE_LIMIT_LIST.clear();
                    CACHE_SERVICE_LIMIT_LIST.addAll(serviceLimitInfoPoS);
                    //防止并发时限流信息丢失,先保存到临时map
                    SERVICE_LIMIT_INFO_TEMP_MAP.clear();
                    SERVICE_LIMIT_INFO_TEMP_MAP.putAll(serviceLimitInfoPoS.stream()
                            .collect(Collectors.toMap(ServiceLimitInfoPO::getSliServiceBeanName, ServiceLimitInfoModel::convert)));
                    SERVICE_LIMIT_INFO_MAP.clear();
                    SERVICE_LIMIT_INFO_MAP.putAll(SERVICE_LIMIT_INFO_TEMP_MAP);
                }
            } catch (Exception exception) {
                CommandLog.errorThrow("获取限流信息异常", exception);
            }
        }, 0L, 10L, TimeUnit.MINUTES);
    }

    /**
     * 初始化服务码及对应消费者信息
     */
    public void initServiceInfo() {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                CommandLog.info("获取服务码信息");
                List<ServiceInfoPO> serviceInfoPoS = repository.queryServiceAndConsumers();
                if (!CollectionUtils.isEmpty(serviceInfoPoS)
                        || serviceInfoPoS.hashCode() != CACHE_SERVICE_LIST.hashCode()
                        || serviceInfoPoS.size() != CACHE_SERVICE_LIST.size()) {
                    CommandLog.info("更新服务码信息");
                    CACHE_SERVICE_LIST.clear();
                    CACHE_SERVICE_LIST.addAll(serviceInfoPoS);
                    SERVICE_TEMP_MAP.clear();
                    SERVICE_TEMP_MAP.putAll(serviceInfoPoS
                            .stream()
                            .collect(Collectors.toMap(ServiceInfoPO::getTsiServiceBeanName, ServiceInfoModel::convert)));
                    SERVICE_MAP.clear();
                    SERVICE_MAP.putAll(SERVICE_TEMP_MAP);
                }
            } catch (Exception exception) {
                //不用try-catch异常了不会抛日志
                CommandLog.errorThrow("获取服务码异常", exception);
            }
        }, 0L, 10L, TimeUnit.MINUTES);
    }

    /**
     * 限流、降级过滤
     *
     * @param response 响应信息
     * @param srvCode  服务码
     * @return true : 进行限流或降级 false : 未配置限流或降级
     */
    private boolean buildLimitResponse(ServerHttpResponse response, String srvCode) {
        ServiceLimitInfoModel infoModel = getServiceLimit(srvCode);
        if (Objects.isNull(infoModel)) {
            return false;
        }

        if (!StringUtils.isEmpty(infoModel.getServiceFallback())) {
            //存在降级信息
            //设置状态码为303,用于get类型转发,未上送服务码,返回错误信息
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(LOCATION, infoModel.getServiceFallback());
            return true;
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
        }

        return false;
    }

    /**
     * 构建正确路由响应
     *
     * @param response 响应信息
     * @param exchange 请求
     * @param srvCode  服务码
     */
    private void buildRouteResponse(ServerHttpResponse response, ServerWebExchange exchange, String srvCode, String method) {
        ServiceInfoModel model = getServiceInfo(srvCode);
        String forwardPath = BusinessConstant.FORWARD_SLASH_STR.concat(model.getServiceName());
        String methodPath;
        //服务码存在#,表示路由到指定方法
        String postPaths = model.getPostPath();
        if (!StringUtils.isEmpty(method) && postPaths.contains(method)) {
            methodPath = Arrays.stream(postPaths.split(BusinessConstant.SPLIT_LINE, -1))
                    .filter(path -> path.contains(method))
                    .map(methodPath1 -> methodPath1.split(BusinessConstant.SHARE_STR, -1)[1])
                    .findFirst()
                    .orElse(BusinessConstant.EMPTY_STR);
        } else if (postPaths.contains(method)) {
            methodPath = Arrays.stream(postPaths.split(BusinessConstant.SPLIT_LINE, -1))
                    .findFirst()
                    .map(methodPath1 -> methodPath1.split(BusinessConstant.SHARE_STR, -1)[1])
                    .orElse(BusinessConstant.EMPTY_STR);
        } else {
            buildServiceNotFoundResponse(response, srvCode);
            return;
        }

        if (!StringUtils.isEmpty(methodPath)) {
            forwardPath = forwardPath.concat(methodPath);
        }

        CommandLog.info("服务化路由 路由URL:{}", forwardPath);
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
        String dbConsumerHost = getServiceInfo(srvCode).getConsumerHost();

        //如果获取到的是域名,转换为ip地址
        if (!switchHost(consumerHost)) {
            consumerHost = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        //服务鉴权
        if (StringUtils.isEmpty(dbConsumerHost) || !dbConsumerHost.contains(consumerHost)) {
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
     * @param srvCode  服务码
     * @return true : 服务不存在
     */
    private boolean buildServiceNotFoundResponse(ServerHttpResponse response, String srvCode) {
        if (StringUtils.isEmpty(srvCode)
                || Objects.isNull(getServiceInfo(srvCode))) {
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
    @RequestMapping(value = GW_ERROR, method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<OutDTO> gatewayError() {
        return Mono.just(GATEWAY_ERROR_OUT_DTO);
    }

    /**
     * 服务不存在错误信息
     *
     * @return 返回服务不存在错误信息
     */
    @RequestMapping(value = SERVICE_NO_FOUND, method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<OutDTO> serviceNoFound() {
        return Mono.just(SERVICE_NOT_FOUND_OUT_DTO);
    }

    /**
     * 服务鉴权失败
     *
     * @return 返回服务鉴权失败
     */
    @RequestMapping(value = SERVICE_NO_AUTH, method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<OutDTO> serviceNoAuth() {
        return Mono.just(SERVICE_NO_AUTH_OUT_DTO);
    }

    /**
     * 服务鉴权失败
     *
     * @return 返回服务鉴权失败
     */
    @RequestMapping(value = SERVICE_LIMIT, method = {RequestMethod.GET, RequestMethod.POST})
    public Mono<OutDTO> serviceLimit() {
        return Mono.just(SERVICE_LIMIT_ERR_OUT_DTO);
    }

    /**
     * 判断是IP地址还是域名
     *
     * @param host 入参host
     * @return true : ip false : 域名
     */
    private boolean switchHost(String host) {
        //从本地localhost请求获取不到真正的机器IP，暂时放行localhost
        String localhost = "localhost";
        return IPV4_PATTERN.matcher(host).matches() || IPV6_PATTERN.matcher(host).matches() || localhost.equals(host);
    }

    /**
     * 根据服务码获取服务信息,不存在则返回空
     * @param srvCode   服务码
     * @return com.evy.linlin.gateway.filter.tunnel.ServiceInfoModel
     */
    private ServiceInfoModel getServiceInfo(String srvCode) {
        ServiceInfoModel model = SERVICE_MAP.get(srvCode);
        if (Objects.isNull(model)) {
            model = SERVICE_TEMP_MAP.get(srvCode);
        }

        return model;
    }

    /**
     * 根据服务码获取服务限流信息,不存在则返回空
     * @param srvCode   服务码
     * @return com.evy.linlin.gateway.filter.tunnel.ServiceLimitInfoModel
     */
    private ServiceLimitInfoModel getServiceLimit(String srvCode) {
        ServiceLimitInfoModel infoModel = SERVICE_LIMIT_INFO_MAP.get(srvCode);
        if (Objects.isNull(infoModel)) {
            infoModel = SERVICE_LIMIT_INFO_TEMP_MAP.get(srvCode);
        }

        return infoModel;
    }
}
