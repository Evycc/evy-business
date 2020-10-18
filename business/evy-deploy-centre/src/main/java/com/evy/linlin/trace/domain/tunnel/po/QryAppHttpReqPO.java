package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:43
 */
@AllArgsConstructor
@Getter
@ToString
public class QryAppHttpReqPO {
    private final String appIp;
    private final Integer limit;
}
