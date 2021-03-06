package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:27
 */
public class SrvInfoPO {
    private String srvCode;
    private String srvName;
    private String providerName;
    private String consumerName;
    private Integer qps;
    private String fallback;
    private Integer srvTimeout;

    public SrvInfoPO(String srvCode, String srvName, String providerName, String consumerName, Integer qps, String fallback, Integer srvTimeout) {
        this.srvCode = srvCode;
        this.srvName = srvName;
        this.providerName = providerName;
        this.consumerName = consumerName;
        this.qps = qps;
        this.fallback = fallback;
        this.srvTimeout = srvTimeout;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getSrvName() {
        return srvName;
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

    @Override
    public String toString() {
        return "SrvInfoPO{" +
                "srvCode='" + srvCode + '\'' +
                ", srvName='" + srvName + '\'' +
                ", providerName='" + providerName + '\'' +
                ", consumerName='" + consumerName + '\'' +
                ", qps=" + qps +
                ", fallback='" + fallback + '\'' +
                ", srvTimeout=" + srvTimeout +
                '}';
    }
}
