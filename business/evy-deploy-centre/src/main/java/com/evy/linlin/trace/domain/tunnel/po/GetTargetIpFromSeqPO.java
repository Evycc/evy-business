package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:43
 */
@AllArgsConstructor
@Getter
@ToString
public class GetTargetIpFromSeqPO {
    private final String seq;
    private final String userSeq;
}
