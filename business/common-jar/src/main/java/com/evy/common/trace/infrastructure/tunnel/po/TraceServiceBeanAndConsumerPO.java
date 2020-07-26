package com.evy.common.trace.infrastructure.tunnel.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 15:19
 */
@Getter
@Setter
@ToString
public class TraceServiceBeanAndConsumerPO {
    private String tsiServiceBeanName;
    private String tsiConsumer;
}
