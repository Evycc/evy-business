package com.evy.linlin.gateway;

import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.trace.TraceLogUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 提供Fegin远程服务调用功能，通过指定服务码进行调用
 * @Author: EvyLiuu
 * @Date: 2020/11/26 23:42
 */
@Service
@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
public class EvyGatewayRequestUtils<T extends GatewayInputDTO, R extends GatewayOutDTO> {
    @Autowired
    private RestTemplate restTemplate;

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
     * 通过服务码调用指定服务
     * @param body 请求DTO com.evy.linlin.gateway.GatewayInputDTO
     * @return com.evy.linlin.gateway.GatewayOutDTO
     */
    public R requestJson(@RequestBody T body, Class<R> responseCls) {
        if (Objects.nonNull(body)) {
            String traceId = TraceLogUtils.buildServiceTraceId();
            body.setTraceId(traceId);
        }
        return restTemplate.postForObject(buildGatewayReqUrl(), body, responseCls);
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
