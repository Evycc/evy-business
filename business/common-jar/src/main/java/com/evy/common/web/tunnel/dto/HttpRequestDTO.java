package com.evy.common.web.tunnel.dto;

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
public class HttpRequestDTO<T extends HttpRequestBase> {
    private String url;
    private T requestBase;
    private HttpEntity params;
    private List<NameValuePair> nvparams;
    private List<Header> headers;

    public static <T extends HttpRequestBase> HttpRequestDTO<T> create(String url, T requestBase, HttpEntity params, List<NameValuePair> nvparams, List<Header> headers) {
        return new HttpRequestDTO<>(url, requestBase, params, nvparams, headers);
    }

    public HttpRequestDTO(String url, T requestBase, HttpEntity params, List<NameValuePair> nvparams, List<Header> headers) {
        this.url = url;
        this.requestBase = requestBase;
        this.params = params;
        this.nvparams = nvparams;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getRequestBase() {
        return requestBase;
    }

    public void setRequestBase(T requestBase) {
        this.requestBase = requestBase;
    }

    public HttpEntity getParams() {
        return params;
    }

    public void setParams(HttpEntity params) {
        this.params = params;
    }

    public List<NameValuePair> getNvparams() {
        return nvparams;
    }

    public void setNvparams(List<NameValuePair> nvparams) {
        this.nvparams = nvparams;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }
}
