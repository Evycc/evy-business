package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:39
 */
@Getter
@AllArgsConstructor
public class QryAppSlowSqlListDO {
    private String buildSeq;
    private String userSeq;
    private Integer limit;
}
