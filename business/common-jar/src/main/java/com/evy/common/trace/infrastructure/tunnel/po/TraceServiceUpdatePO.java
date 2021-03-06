package com.evy.common.trace.infrastructure.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 16:27
 */
public class TraceServiceUpdatePO {
    private String tsiServiceBeanName;
    private String tsiProviderName;
    private String tsiServiceName;
    private String tsiConsumerName;
    private String tsiProvider;
    private String tsiServicePath;
    private String tsiConsumer;

    public TraceServiceUpdatePO() {
    }

    public TraceServiceUpdatePO(String tsiServiceBeanName, String tsiProviderName, String tsiServiceName, String tsiConsumerName, String tsiProvider, String tsiServicePath, String tsiConsumer) {
        this.tsiServiceBeanName = tsiServiceBeanName;
        this.tsiProviderName = tsiProviderName;
        this.tsiServiceName = tsiServiceName;
        this.tsiConsumerName = tsiConsumerName;
        this.tsiProvider = tsiProvider;
        this.tsiServicePath = tsiServicePath;
        this.tsiConsumer = tsiConsumer;
    }

    private TraceServiceUpdatePO(String tsiServiceBeanName, String tsiProviderName, String tsiServiceName, String tsiProvider, String tsiServicePath) {
        this.tsiServiceBeanName = tsiServiceBeanName;
        this.tsiProviderName = tsiProviderName;
        this.tsiServiceName = tsiServiceName;
        this.tsiProvider = tsiProvider;
        this.tsiServicePath = tsiServicePath;
    }

    private TraceServiceUpdatePO(String var1, String var2, int flag) {
        if (flag == 1) {
            this.tsiServiceBeanName = var1;
        } else {
            this.tsiProviderName = var1;
        }
        this.tsiConsumerName = var2;
    }

    public static TraceServiceUpdatePO createUpdateProvider(String tsiServiceBeanName, String tsiProviderName, String tsiServiceName, String tsiProvider, String tsiServicePath) {
        return new TraceServiceUpdatePO(tsiServiceBeanName, tsiProviderName, tsiServiceName, tsiProvider, tsiServicePath);
    }

    public static TraceServiceUpdatePO createUpdateConsumer(String tsiServiceBeanName, String tsiConsumerName, String tsiConsumer) {
        TraceServiceUpdatePO updatePo = new TraceServiceUpdatePO(tsiServiceBeanName, tsiConsumerName, 1);
        updatePo.tsiConsumer = tsiConsumer;
        return updatePo;
    }

    public static TraceServiceUpdatePO createCleanIp(String tsiProviderName, String tsiConsumerName) {
        return new TraceServiceUpdatePO(tsiProviderName, tsiConsumerName, 0);
    }

    public static TraceServiceUpdatePO createTsiProvider(String tsiProvider) {
        TraceServiceUpdatePO updatePo = new TraceServiceUpdatePO();
        updatePo.tsiProvider = tsiProvider;
        return updatePo;
    }
}
