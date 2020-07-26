package com.evy.linlin.filter.repository.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/25 21:20
 */
public class ServiceInfoPO {
    private String tsiServiceBeanName;
    private String tsiProvider;
    private String tsiConsumerNames;
    private String tsiServicePath;

    public String getTsiServiceBeanName() {
        return tsiServiceBeanName;
    }

    public void setTsiServiceBeanName(String tsiServiceBeanName) {
        this.tsiServiceBeanName = tsiServiceBeanName;
    }

    public String getTsiProvider() {
        return tsiProvider;
    }

    public String getTsiServicePath() {
        return tsiServicePath;
    }

    public void setTsiServicePath(String tsiServicePath) {
        this.tsiServicePath = tsiServicePath;
    }

    public void setTsiProvider(String tsiProvider) {
        this.tsiProvider = tsiProvider;
    }

    public String getTsiConsumerNames() {
        return tsiConsumerNames;
    }

    public void setTsiConsumerNames(String tsiConsumerNames) {
        this.tsiConsumerNames = tsiConsumerNames;
    }
}
