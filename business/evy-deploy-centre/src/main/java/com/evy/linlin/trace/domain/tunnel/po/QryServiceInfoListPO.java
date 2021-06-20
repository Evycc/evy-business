package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:32
 */
public class QryServiceInfoListPO {
    private String tsiServiceBeanName;
    private String tsiServiceName;
    private String tsiServicePath;
    private String tsiProvider;
    private String tsiConsumer;
    private String tsiProviderNames;
    private String tsiConsumerNames;
    private String gmtModify;
    private String tsiTimeout;

    public String getTsiServiceBeanName() {
        return tsiServiceBeanName;
    }

    public String getTsiServiceName() {
        return tsiServiceName;
    }

    public String getTsiServicePath() {
        return tsiServicePath;
    }

    public String getTsiProvider() {
        return tsiProvider;
    }

    public String getTsiConsumer() {
        return tsiConsumer;
    }

    public String getTsiProviderNames() {
        return tsiProviderNames;
    }

    public String getTsiConsumerNames() {
        return tsiConsumerNames;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    public String getTsiTimeout() {
        return tsiTimeout;
    }

    @Override
    public String toString() {
        return "QryServiceInfoListPO{" +
                "tsiServiceBeanName='" + tsiServiceBeanName + '\'' +
                ", tsiServiceName='" + tsiServiceName + '\'' +
                ", tsiServicePath='" + tsiServicePath + '\'' +
                ", tsiProvider='" + tsiProvider + '\'' +
                ", tsiConsumer='" + tsiConsumer + '\'' +
                ", tsiProviderNames='" + tsiProviderNames + '\'' +
                ", tsiConsumerNames='" + tsiConsumerNames + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                ", tsiTimeout='" + tsiTimeout + '\'' +
                '}';
    }
}
