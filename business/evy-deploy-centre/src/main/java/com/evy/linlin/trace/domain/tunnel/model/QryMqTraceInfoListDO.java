package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:04
 */
public class QryMqTraceInfoListDO {
    private final String seq;
    private final String userSeq;
    /**
     * topic
     */
    private final String topic;
    private final String msgId;
    private final Integer limit;

    public QryMqTraceInfoListDO(String seq, String userSeq, String topic, String msgId, Integer limit) {
        this.seq = seq;
        this.userSeq = userSeq;
        this.topic = topic;
        this.msgId = msgId;
        this.limit = limit;
    }

    public String getSeq() {
        return seq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getTopic() {
        return topic;
    }

    public String getMsgId() {
        return msgId;
    }

    public Integer getLimit() {
        return limit;
    }
}
