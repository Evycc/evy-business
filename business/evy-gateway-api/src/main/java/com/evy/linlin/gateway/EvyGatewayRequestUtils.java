package com.evy.linlin.gateway;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.config.BusinessProperties;
import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.po.ErrorInfoPO;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceLogUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 提供Fegin远程服务调用功能，通过指定服务码进行调用
 * @Author: EvyLiuu
 * @Date: 2020/11/26 23:42
 */
@Service
@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
public class EvyGatewayRequestUtils {
    /**
     * 默认RestTemplate
     */
    @Autowired
    private RestTemplate defaultRestTemplate;
    @Autowired
    private BusinessProperties properties;
    /**
     * 根据服务超时时间存储RestTemplate
     */
    private static final Map<Integer, RestTemplate> TIME_TEMPLATE_MAP = new HashMap<>(16);
    /**
     * 根据服务码存储对应超时时间
     */
    private static final Map<String, Integer> SRV_TIMEOUT_MAP = new HashMap<>(16);
    /**
     * 查询服务超时时间
     */
    private static final String QRY_ALL_SRV_TIMEOUT_SQL = "ServiceInfoMapper.qryAllSrvTimeoutList";
    private static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE = CreateFactory.returnScheduledExecutorService("TimeOutRestTemplate-ThreadTask", 1);

    {
        //定时监控队列，存在数据则进行处理后入库
        //1分钟后执行
        long initialDelay = 0L;
        //间隔10分钟轮询
        long delay = 600000L;
        EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                List<SrvTimeoutModel> timeoutList = DBUtils.selectList(QRY_ALL_SRV_TIMEOUT_SQL);
                if (!CollectionUtils.isEmpty(timeoutList)) {
                    //更新服务码与超时时间对应关系
                    timeoutList.stream()
                            .filter(model -> !SRV_TIMEOUT_MAP.containsKey(model.getSrvCode()) ||
                                    !SRV_TIMEOUT_MAP.get(model.getSrvCode()).equals(model.getTimeout()))
                            .forEach(model -> SRV_TIMEOUT_MAP.put(model.getSrvCode(), model.getTimeout()));

                    //根据超时时间,添加RestTemplate
                    timeoutList.stream()
                            .filter(model -> !TIME_TEMPLATE_MAP.containsKey(model.getTimeout()))
                            .forEach(model -> {
                                BusinessProperties.Http http = properties.getHttp();
                                int connTimeout = http.getConnTimeOut();

                                RequestConfig requestConfig = RequestConfig.custom()
                                        //请求连接超时
                                        .setConnectTimeout(connTimeout)
                                        //获取连接池连接的超时时间
                                        .setConnectionRequestTimeout(connTimeout)
                                        //响应超时
                                        .setSocketTimeout(model.getTimeout())
                                        .build();
                                HttpClient httpClient = HttpClientBuilder.create()
                                        .setDefaultRequestConfig(requestConfig)
                                        .setRedirectStrategy(new LaxRedirectStrategy())
                                        .build();

                                RestTemplate restTemplate = new RestTemplate();
                                HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
                                factory.setHttpClient(httpClient);
                                restTemplate.setRequestFactory(factory);

                                TIME_TEMPLATE_MAP.put(model.getTimeout(), restTemplate);
                            });
                }
            } catch (Exception e) {
                CommandLog.errorThrow("{}", e);
            }
        }, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    @Bean
    @ConditionalOnMissingBean({RestTemplate.class})
    @LoadBalanced
    public RestTemplate getRestTemplate(HttpClient httpClient) {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    /**
     * 获取服务超时时间
     * @param srvCode 服务码
     * @return 超时时间,单位ms
     */
    public static Integer getSrvTimeout(String srvCode) {
        Integer timeout = SRV_TIMEOUT_MAP.get(srvCode);
        CommandLog.info("{}", SRV_TIMEOUT_MAP);
        CommandLog.info("服务码:{} 超时时间:{}", srvCode, timeout);
        return timeout;
    }

    /**
     * 通过服务码调用指定服务
     * @param body 请求DTO com.evy.linlin.gateway.GatewayInputDTO
     * @return com.evy.linlin.gateway.GatewayOutDTO
     */
    public <T extends GatewayInputDTO, R extends GatewayOutDTO> R requestJson(@RequestBody T body, Class<R> responseCls) throws BasicException {
        R result;
        RestTemplate restTemplate = null;

        try {
            if (Objects.nonNull(body)) {
                String traceId = TraceLogUtils.buildTraceId();
                body.setTraceId(traceId);

                //根据服务超时时间,选择对应RestTemplate
                //服务码ServiceCode可能的格式：ServiceCode#reqPath
                String srvCode = body.getServiceCode().split(BusinessConstant.SHARE_STR, -1)[0];
                if (SRV_TIMEOUT_MAP.containsKey(srvCode)) {
                    restTemplate = TIME_TEMPLATE_MAP.get(getSrvTimeout(srvCode));
                }
            }
            if (Objects.isNull(restTemplate)) {
                restTemplate = defaultRestTemplate;
            }
            result = restTemplate.postForObject(buildGatewayReqUrl(), body, responseCls);
        } catch (RestClientException e) {
            if (Objects.nonNull(e.getCause())) {
                Throwable throwable = e.getCause();
                while (Objects.nonNull(throwable.getCause())) {
                    throwable = throwable.getCause();
                }
                if (throwable instanceof SocketTimeoutException) {
                    throw new BasicException(ErrorConstant.SERVICE_TIME_OUT_ERR);
                } else {
                    throw new BasicException(ErrorConstant.ERROR_01);
                }
            } else {
                throw new BasicException(e);
            }
        }

        return result;
    }

    /**
     * 构建网关请求URL
     * @return java.net.URI
     */
    private static URI buildGatewayReqUrl() {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder()
                    .setScheme("http")
                    .setHost(GatewayConstant.EVY_GATEWAY)
                    .setPath(GatewayConstant.GATEWAY_PATH)
                    .setCharset(StandardCharsets.UTF_8);
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            uri = URI.create("http://" + GatewayConstant.EVY_GATEWAY + GatewayConstant.GATEWAY_PATH);
        }

        return uri;
    }
}
