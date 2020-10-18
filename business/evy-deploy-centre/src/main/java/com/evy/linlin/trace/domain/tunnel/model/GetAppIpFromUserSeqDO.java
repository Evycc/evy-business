package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:28
 */
@Getter
@AllArgsConstructor
public class GetAppIpFromUserSeqDO {
    private final String seq;
    private final String userSeq;
}
