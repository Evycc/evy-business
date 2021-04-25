package com.evy.common.trace.infrastructure.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 15:10
 */
public class TraceServiceBeanNamePO {
    private String tsiServiceBeanName;

    @Override
    public String toString() {
        return "TraceServiceBeanNamePO{" +
                "tsiServiceBeanName='" + tsiServiceBeanName + '\'' +
                '}';
    }

    public String getTsiServiceBeanName() {
        return tsiServiceBeanName;
    }

    public void setTsiServiceBeanName(String tsiServiceBeanName) {
        this.tsiServiceBeanName = tsiServiceBeanName;
    }
}
