package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:27
 */
@Getter
@AllArgsConstructor
@ToString
public class SrvInfoPO {
    private String srvCode;
    private String srvName;
    private String providerName;
    private String consumerName;
    private Integer qps;
    private String fallback;
}
