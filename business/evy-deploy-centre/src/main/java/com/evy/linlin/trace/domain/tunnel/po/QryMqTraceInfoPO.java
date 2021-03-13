package com.evy.linlin.trace.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:57
 */
@AllArgsConstructor
@Getter
@ToString
public class QryMqTraceInfoPO {
    private final List<String> appIp;
    private final String topic;
    private final String msgId;
    private final Integer limit;
}
