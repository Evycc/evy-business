package com.evy.linlin.gateway;

/**
 * @Author: EvyLiuu
 * @Date: 2021/6/13 13:23
 */
public class SrvTimeoutModel {
    private Integer timeout;
    private String srvCode;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public void setSrvCode(String srvCode) {
        this.srvCode = srvCode;
    }
}
