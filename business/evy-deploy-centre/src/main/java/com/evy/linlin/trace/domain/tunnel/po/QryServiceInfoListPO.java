package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:32
 */
@Getter
@ToString
public class QryServiceInfoListPO {
    private String tsiServiceBeanName;
    private String tsiServiceName;
    private String tsiServicePath;
    private String tsiProvider;
    private String tsiConsumer;
    private String tsiProviderNames;
    private String tsiConsumerNames;
    private String gmtModify;
}
