package com.evy.common.http.tunnel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.List;

/**
 * 构建Http请求DTO
 * @Author: EvyLiuu
 * @Date: 2020/5/24 14:48
 */
@AllArgsConstructor
@Getter
@Setter
public class HttpRequestDTO<T extends HttpRequestBase> {
    private String url;
    private T requestBase;
    private HttpEntity params;
    private List<NameValuePair> nvparams;
    private List<Header> headers;

    public static <T extends HttpRequestBase> HttpRequestDTO<T> create(String url, T requestBase, HttpEntity params, List<NameValuePair> nvparams, List<Header> headers) {
        return new HttpRequestDTO<>(url, requestBase, params, nvparams, headers);
    }
}
