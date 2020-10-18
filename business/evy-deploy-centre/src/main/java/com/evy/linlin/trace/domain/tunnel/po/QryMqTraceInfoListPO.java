package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:57
 */
@Getter
@ToString
public class QryMqTraceInfoListPO {
    private String tmfReqIp;
    private String tmfTopic;
    private String tmfTag;
    private String tmfMsgId;
    private String tmfMqContent;
    private String tmfRespIp;
    private String tmfProducerStartTimestamp;
    private String tmfConsumerStartTimestamp;
    private String tmfConsumerTakeupTimestamp;
    private String gmtModify;
}
