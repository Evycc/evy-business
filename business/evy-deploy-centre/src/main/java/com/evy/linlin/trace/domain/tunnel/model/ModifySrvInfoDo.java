package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 21:14
 */
@Getter
@AllArgsConstructor
public class ModifySrvInfoDo {
    private String srvCode;
    private String serviceName;
    private String providerName;
    private String consumerName;
    private Integer qps;
    private String fallback;
}
