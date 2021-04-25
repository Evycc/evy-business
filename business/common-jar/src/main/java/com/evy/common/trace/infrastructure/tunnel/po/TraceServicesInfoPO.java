package com.evy.common.trace.infrastructure.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 15:11
 */
public class TraceServicesInfoPO {
    private final String tsiProvider;

    private TraceServicesInfoPO(String tsiProvider) {
        this.tsiProvider = tsiProvider;
    }

    public static TraceServicesInfoPO create(String tsiProvider) {
        return new TraceServicesInfoPO(tsiProvider);
    }

    public String getTsiProvider() {
        return tsiProvider;
    }
}
