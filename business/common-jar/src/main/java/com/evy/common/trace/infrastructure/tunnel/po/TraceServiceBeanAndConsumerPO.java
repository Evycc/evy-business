package com.evy.common.trace.infrastructure.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 15:19
 */
public class TraceServiceBeanAndConsumerPO {
    private String tsiServiceBeanName;
    private String tsiConsumer;

    @Override
    public String toString() {
        return "TraceServiceBeanAndConsumerPO{" +
                "tsiServiceBeanName='" + tsiServiceBeanName + '\'' +
                ", tsiConsumer='" + tsiConsumer + '\'' +
                '}';
    }

    public String getTsiServiceBeanName() {
        return tsiServiceBeanName;
    }

    public void setTsiServiceBeanName(String tsiServiceBeanName) {
        this.tsiServiceBeanName = tsiServiceBeanName;
    }

    public String getTsiConsumer() {
        return tsiConsumer;
    }

    public void setTsiConsumer(String tsiConsumer) {
        this.tsiConsumer = tsiConsumer;
    }
}
