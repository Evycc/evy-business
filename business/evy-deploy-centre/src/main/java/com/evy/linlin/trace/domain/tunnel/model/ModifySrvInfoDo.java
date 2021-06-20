package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 21:14
 */
public class ModifySrvInfoDo {
    private String srvCode;
    private String serviceName;
    private String providerName;
    private String consumerName;
    private Integer qps;
    private String fallback;
    private Integer srvTimeout;

    public ModifySrvInfoDo(String srvCode, String serviceName, String providerName, String consumerName, Integer qps, String fallback, Integer srvTimeout) {
        this.srvCode = srvCode;
        this.serviceName = serviceName;
        this.providerName = providerName;
        this.consumerName = consumerName;
        this.qps = qps;
        this.fallback = fallback;
        this.srvTimeout = srvTimeout;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public Integer getQps() {
        return qps;
    }

    public String getFallback() {
        return fallback;
    }

    public Integer getSrvTimeout() {
        return srvTimeout;
    }
}
