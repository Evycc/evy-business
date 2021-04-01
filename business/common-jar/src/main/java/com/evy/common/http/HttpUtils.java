package com.evy.common.http;

import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.http.tunnel.dto.HttpRequestDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceLogUtils;
import com.evy.common.trace.TraceUtils;
import com.evy.common.utils.AppContextUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Http请求工具
 * @Author: EvyLiuu
 * @Date: 2020/5/24 14:40
 */
@Component
@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
public class HttpUtils {
    /**
     * Basic Authorization 认证 value值
     */
    private static final String BASIC_AUTH_VALUE = "Basic ";
    /**
     * Basic Authorization 认证 header
     */
    private static final String BASIC_AUTH_HEADER = "Authorization";

    /**
     * 发起Http请求
     * @param httpRequestDTO    请求内容实体
     * @param function  响应处理方法
     * @param <T>   返回结果
     * @param <P>   org.apache.http.client.methods.HttpRequestBase 子类
     * @return  返回结果
     * @throws URISyntaxException   URI构建异常
     * @throws IOException  http请求异常
     */
    public <T, P extends HttpRequestBase> T httpRequest(HttpRequestDTO<P> httpRequestDTO,
                                                               Function<HttpResponse, T> function) throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = AppContextUtils.getBean(CloseableHttpClient.class);
        HttpRequestBase httpRequestBase = httpRequestDTO.getRequestBase();
        List<NameValuePair> nameValuePairList = httpRequestDTO.getNvparams();
        List<Header> headers = httpRequestDTO.getHeaders();
        HttpEntity httpEntity = httpRequestDTO.getParams();
        String path = httpRequestDTO.getUrl();
        long startTime = System.currentTimeMillis();
        String reqParam = !Objects.isNull(nameValuePairList) ? nameValuePairList.toString() : null;

        //trace
        String traceIdTemp = TraceLogUtils.getCurTraceId();
        String traceId = TraceLogUtils.buildHttpTraceId();
        TraceLogUtils.setHttpTraceId(traceId);

        try {
            URI uri = uriBuilder(path, nameValuePairList).build();
            httpRequestBase.setURI(uri);
            if (!CollectionUtils.isEmpty(headers)) {
                httpRequestBase.setHeaders(headers.toArray(new Header[]{}));
            }

            if (httpRequestBase instanceof HttpEntityEnclosingRequestBase && !Objects.isNull(httpEntity)) {
                //处理非GET类型且带参数的请求
                HttpEntityEnclosingRequestBase noGetRequest = (HttpEntityEnclosingRequestBase) httpRequestBase;
                noGetRequest.setEntity(httpEntity);

                //trace
                try {
                    reqParam = new String(EntityUtils.toByteArray(httpEntity), StandardCharsets.UTF_8);
                } catch (IOException ignored) {
                }
            }

            CommandLog.info("发起请求 => {}", httpRequestBase.getURI());
            HttpResponse response = httpClient.execute(httpRequestBase);
            CommandLog.info("收到响应(耗时: {}ms) <= {}",
                    (System.currentTimeMillis() - startTime), response);
            T object = function.apply(response);

            //trace
            TraceUtils.addTraceHttp(path, (System.currentTimeMillis() - startTime), true, reqParam, String.valueOf(object));

            return object;
        } catch (Exception e) {
            CommandLog.error("构建HTTP请求失败(耗时: {}ms)", (System.currentTimeMillis() - startTime));
            //trace
            TraceUtils.addTraceHttp(path, (System.currentTimeMillis() - startTime), false, reqParam, e.getMessage());
            throw e;
        } finally {
            //trace
            TraceLogUtils.setHttpTraceId(traceId, (System.currentTimeMillis() - startTime));
            TraceLogUtils.rmLogTraceId(traceIdTemp);
        }
    }

    /**
     * 返回Basic Auth认证报文头
     * @param username  账号
     * @param password  密码
     * @return org.apache.http.Header
     */
    public Header buildBasicAuth(String username, String password){
        byte[] value = (username + BusinessConstant.COLON_STR + password).getBytes(StandardCharsets.UTF_8);
        return new BasicHeader(BASIC_AUTH_HEADER,
                BASIC_AUTH_VALUE.concat(Base64.getEncoder().encodeToString(value)));
    }

    public URIBuilder uriBuilder(String path) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(path));
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        return uriBuilder;
    }

    public URIBuilder uriBuilder(String path, List<NameValuePair> pairs) {
        URIBuilder uriBuilder = uriBuilder(path);
        if (!Objects.isNull(pairs)) {
            uriBuilder.addParameters(pairs);
        }
        return uriBuilder;
    }
}
