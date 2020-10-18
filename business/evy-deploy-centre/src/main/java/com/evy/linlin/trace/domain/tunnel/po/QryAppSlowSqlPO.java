package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:41
 */
@Getter
@AllArgsConstructor
@ToString
public class QryAppSlowSqlPO {
    private final String appIp;
    private final Integer limit;
}
