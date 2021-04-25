package com.evy.linlin.trace.domain.tunnel.po;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:57
 */
public class QryMqTraceInfoPO {
    private final List<String> appIp;
    private final String topic;
    private final String msgId;
    private final Integer limit;

    public QryMqTraceInfoPO(List<String> appIp, String topic, String msgId, Integer limit) {
        this.appIp = appIp;
        this.topic = topic;
        this.msgId = msgId;
        this.limit = limit;
    }

    public List<String> getAppIp() {
        return appIp;
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

    @Override
    public String toString() {
        return "QryMqTraceInfoPO{" +
                "appIp=" + appIp +
                ", topic='" + topic + '\'' +
                ", msgId='" + msgId + '\'' +
                ", limit=" + limit +
                '}';
    }
}
