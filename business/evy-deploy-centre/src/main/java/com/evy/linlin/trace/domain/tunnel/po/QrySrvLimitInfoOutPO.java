package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 16:36
 */
public class QrySrvLimitInfoOutPO {
    private String srvCode;
    private String srvName;
    private Integer qpsLimit;
    private String fallback;

    @Override
    public String toString() {
        return "QrySrvLimitInfoOutPO{" +
                "srvCode='" + srvCode + '\'' +
                ", srvName='" + srvName + '\'' +
                ", qpsLimit=" + qpsLimit +
                ", fallback='" + fallback + '\'' +
                '}';
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getSrvName() {
        return srvName;
    }

    public Integer getQpsLimit() {
        return qpsLimit;
    }

    public String getFallback() {
        return fallback;
    }
}
