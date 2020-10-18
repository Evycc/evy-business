package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:58
 */
@AllArgsConstructor
@Getter
public class QryAppThreadInfoListDO {
    private String buildSeq;
    private String userSeq;
    private String threadName;
}
