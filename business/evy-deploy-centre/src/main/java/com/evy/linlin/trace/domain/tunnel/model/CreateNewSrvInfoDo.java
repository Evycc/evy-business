package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 21:12
 */
public class CreateNewSrvInfoDo {
    private String srvCode;
    private String providerName;
    private String consumerName;

    public CreateNewSrvInfoDo(String srvCode, String providerName, String consumerName) {
        this.srvCode = srvCode;
        this.providerName = providerName;
        this.consumerName = consumerName;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getConsumerName() {
        return consumerName;
    }
}
