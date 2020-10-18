package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:04
 */
@AllArgsConstructor
@Getter
public class QryMqTraceInfoListDO {
    private final String seq;
    private final String userSeq;
    /**
     * topic
     */
    private final String topic;
    private final String msgId;
    private final Integer limit;
}
