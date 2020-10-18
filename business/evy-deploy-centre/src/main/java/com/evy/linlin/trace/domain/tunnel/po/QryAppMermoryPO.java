package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:11
 */
@AllArgsConstructor
@Getter
@ToString
public class QryAppMermoryPO {
    private final String appIp;
    private final Integer limit;
}
