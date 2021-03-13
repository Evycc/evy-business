package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:41
 */
@Getter
@AllArgsConstructor
@ToString
public class QryAppSlowSqlPO {
    private final List<String> appIps;
}
