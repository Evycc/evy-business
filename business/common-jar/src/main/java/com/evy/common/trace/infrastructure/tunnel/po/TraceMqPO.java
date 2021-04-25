package com.evy.common.trace.infrastructure.tunnel.po;

/**
 * Trace MQ对象PO类
 * @Author: EvyLiuu
 * @Date: 2020/6/13 20:06
 */
public class TraceMqPO {
    private final String tmfTopic;
    private final String tmfReqIp;
    private final String tmfTag;
    private final String tmfMsgId;
    private final String tmfMsgContent;
    private final String tmfRespIp;
    private final String tmfProducerStartTimestamp;
    private final String tmfConsumerEndTimestamp;
    private final String tmfConsumerTakeUpTimestamp;

    private TraceMqPO(String tmfTopic, String tmfReqIp, String tmfTag, String tmfMsgId, String tmfMsgContent, String tmfRespIp, String tmfProducerStartTimestamp, String tmfConsumerEndTimestamp, String tmfConsumerTakeUpTimestamp) {
        this.tmfTopic = tmfTopic;
        this.tmfReqIp = tmfReqIp;
        this.tmfTag = tmfTag;
        this.tmfMsgId = tmfMsgId;
        this.tmfMsgContent = tmfMsgContent;
        this.tmfRespIp = tmfRespIp;
        this.tmfProducerStartTimestamp = tmfProducerStartTimestamp;
        this.tmfConsumerEndTimestamp = tmfConsumerEndTimestamp;
        this.tmfConsumerTakeUpTimestamp = tmfConsumerTakeUpTimestamp;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceMqPO create(String tmfTopic, String tmfReqIp, String tmfTag, String tmfMsgId, String tmfMsgContent, String tmfRespIp, String tmfProducerStartTimestamp, String tmfConsumerEndTimestamp, String tmfConsumerTakeUpTimestamp) {
        return new TraceMqPO(tmfTopic, tmfReqIp, tmfTag, tmfMsgId, tmfMsgContent, tmfRespIp, tmfProducerStartTimestamp, tmfConsumerEndTimestamp, tmfConsumerTakeUpTimestamp);
    }

    /**
     * 隐藏构造方法细节,构造MQ PRODUCTER参数方法
     */
    public static TraceMqPO createSend(String tmfTopic, String tmfReqIp, String tmfTag, String tmfMsgId, String tmfMsgContent, String tmfProducerStartTimestamp) {
        return new TraceMqPO(tmfTopic, tmfReqIp, tmfTag, tmfMsgId, tmfMsgContent, null, tmfProducerStartTimestamp, null, null);
    }

    /**
     * 隐藏构造方法细节,构造MQ CONSUMER参数方法
     */
    public static TraceMqPO createConsume(String tmfMsgId, String tmfRespIp, String tmfConsumerEndTimestamp, String tmfConsumerTakeUpTimestamp) {
        return new TraceMqPO(null, null, null, tmfMsgId, null, tmfRespIp, null, tmfConsumerEndTimestamp, tmfConsumerTakeUpTimestamp);
    }

    public String getTmfTopic() {
        return tmfTopic;
    }

    public String getTmfReqIp() {
        return tmfReqIp;
    }

    public String getTmfTag() {
        return tmfTag;
    }

    public String getTmfMsgId() {
        return tmfMsgId;
    }

    public String getTmfMsgContent() {
        return tmfMsgContent;
    }

    public String getTmfRespIp() {
        return tmfRespIp;
    }

    public String getTmfProducerStartTimestamp() {
        return tmfProducerStartTimestamp;
    }

    public String getTmfConsumerEndTimestamp() {
        return tmfConsumerEndTimestamp;
    }

    public String getTmfConsumerTakeUpTimestamp() {
        return tmfConsumerTakeUpTimestamp;
    }
}
