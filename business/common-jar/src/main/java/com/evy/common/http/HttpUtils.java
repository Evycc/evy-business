package com.evy.common.http;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.http.tunnel.dto.HttpRequestDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceUtils;
import com.evy.common.utils.AppContextUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
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
 * Http请求工具<br/>
 * 配置项:<br/>
 * business.http.connTimeOut http请求连接超时时间，单位ms，默认10s<br/>
 * business.http.reqTimeOut http请求超时时间，单位ms，默认30s<br/>
 * @Author: EvyLiuu
 * @Date: 2020/5/24 14:40
 */
public class HttpUtils {
    private static final RequestConfig REQUEST_CONFIG;
    private static final String HTTP_CONN_TIMEOUT_PRPO = "business.http.connTimeOut";
    private static final String HTTP_REQ_TIMEOUT_PRPO = "business.http.reqTimeOut";
    /**
     * http请求连接超时时间，单位ms，默认10s
     */
    private static final int HTTP_CONN_TIMEOUT = 10000;
    /**
     * http请求超时时间，单位ms，默认30s
     */
    private static final int HTTP_REQUEST_TIMEOUT = 30000;
    /**
     * Basic Authorization 认证 value值
     */
    private static final String BASIC_AUTH_VALUE = "Basic ";
    /**
     * Basic Authorization 认证 header
     */
    private static final String BASIC_AUTH_HEADER = "Authorization";

    static {
        int connTimeOut = BusinessConstant.SUCESS;
        int reqTimeOut = BusinessConstant.SUCESS;
        try {
            connTimeOut = Integer.parseInt(AppContextUtils.getForEnv(HTTP_CONN_TIMEOUT_PRPO));
            reqTimeOut = Integer.parseInt(AppContextUtils.getForEnv(HTTP_REQ_TIMEOUT_PRPO));
        } catch (Exception e) {
            CommandLog.error("HttpUtils获取超时配置失败,取用默认值 {}", e.getMessage());
        } finally {
            REQUEST_CONFIG = RequestConfig.custom()
                    //请求连接超时
                    .setConnectTimeout(connTimeOut == BusinessConstant.ONE_NUM ?
                            HTTP_CONN_TIMEOUT : connTimeOut)
                    //获取连接池连接的超时时间
                    .setConnectionRequestTimeout(HTTP_CONN_TIMEOUT)
                    //响应超时
                    .setSocketTimeout(reqTimeOut == BusinessConstant.ONE_NUM ?
                            HTTP_REQUEST_TIMEOUT : reqTimeOut)
                    .build();
        }
    }

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
    public static <T, P extends HttpRequestBase> T httpRequest(HttpRequestDTO<P> httpRequestDTO,
                                                               Function<HttpResponse, T> function) throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpRequestBase httpRequestBase = httpRequestDTO.getRequestBase();
        List<NameValuePair> nameValuePairList = httpRequestDTO.getNvparams();
        List<Header> headers = httpRequestDTO.getHeaders();
        HttpEntity httpEntity = httpRequestDTO.getParams();
        String path = httpRequestDTO.getUrl();
        long startTime = System.currentTimeMillis();
        String reqParam = !Objects.isNull(nameValuePairList) ? nameValuePairList.toString() : null;

        try {
            URI uri = uriBuilder(path, nameValuePairList).build();
            httpRequestBase.setURI(uri);
            httpRequestBase.setConfig(REQUEST_CONFIG);
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
        }
    }

    /**
     * 返回Basic Auth认证报文头
     * @param username  账号
     * @param password  密码
     * @return org.apache.http.Header
     */
    public static Header buildBasicAuth(String username, String password){
        byte[] value = (username + BusinessConstant.COLON_STR + password).getBytes(StandardCharsets.UTF_8);
        return new BasicHeader(BASIC_AUTH_HEADER,
                BASIC_AUTH_VALUE.concat(Base64.getEncoder().encodeToString(value)));
    }

    public static URIBuilder uriBuilder(String path) {
        URIBuilder uriBuilder = new URIBuilder(URI.create(path));
        uriBuilder.setCharset(StandardCharsets.UTF_8);
        return uriBuilder;
    }

    public static URIBuilder uriBuilder(String path, List<NameValuePair> pairs) {
        URIBuilder uriBuilder = uriBuilder(path);
        if (!Objects.isNull(pairs)) {
            uriBuilder.addParameters(pairs);
        }
        return uriBuilder;
    }
}
