package com.evy.linlin.trace.domain.tunnel.po;

import java.util.Arrays;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:57
 */
public class QryMqTraceInfoListPO {
    private String tmfReqIp;
    private String tmfTopic;
    private String tmfTag;
    private String tmfMsgId;
    private byte[] tmfMqContent;
    private String tmfRespIp;
    private String tmfProducerStartTimestamp;
    private String tmfConsumerStartTimestamp;
    private String tmfConsumerTakeupTimestamp;
    private String gmtModify;

    @Override
    public String toString() {
        return "QryMqTraceInfoListPO{" +
                "tmfReqIp='" + tmfReqIp + '\'' +
                ", tmfTopic='" + tmfTopic + '\'' +
                ", tmfTag='" + tmfTag + '\'' +
                ", tmfMsgId='" + tmfMsgId + '\'' +
                ", tmfMqContent=" + Arrays.toString(tmfMqContent) +
                ", tmfRespIp='" + tmfRespIp + '\'' +
                ", tmfProducerStartTimestamp='" + tmfProducerStartTimestamp + '\'' +
                ", tmfConsumerStartTimestamp='" + tmfConsumerStartTimestamp + '\'' +
                ", tmfConsumerTakeupTimestamp='" + tmfConsumerTakeupTimestamp + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }

    public String getTmfReqIp() {
        return tmfReqIp;
    }

    public String getTmfTopic() {
        return tmfTopic;
    }

    public String getTmfTag() {
        return tmfTag;
    }

    public String getTmfMsgId() {
        return tmfMsgId;
    }

    public byte[] getTmfMqContent() {
        return tmfMqContent;
    }

    public String getTmfRespIp() {
        return tmfRespIp;
    }

    public String getTmfProducerStartTimestamp() {
        return tmfProducerStartTimestamp;
    }

    public String getTmfConsumerStartTimestamp() {
        return tmfConsumerStartTimestamp;
    }

    public String getTmfConsumerTakeupTimestamp() {
        return tmfConsumerTakeupTimestamp;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
