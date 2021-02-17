package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 21:41
 */
@AllArgsConstructor
@Getter
public class QryHttpReqInfoListDO {
    private final String seq;
    private final String userSeq;
    private final String path;
    private final int limit;
}
