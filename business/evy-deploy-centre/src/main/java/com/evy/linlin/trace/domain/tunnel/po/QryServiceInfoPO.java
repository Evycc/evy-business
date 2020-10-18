package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:30
 */
@AllArgsConstructor
@Getter
@ToString
public class QryServiceInfoPO {
    private final String appIp;
    private final String serviceBeanName;
}
