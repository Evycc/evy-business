package com.evy.common.trace.infrastructure.tunnel.model;

import com.evy.common.utils.DateUtils;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/6/7 20:29
 */
@Getter
@ToString
public class TraceMqModel extends TraceModel {
    private String topic;
    private String tag;
    private String reqTimestamp;
    private String msgId;
    private String mqContent;

    public TraceMqModel(String reqIp, long takeUpTimestamp, String topic, String tag, String reqTimestamp, String msgId, String mqContent) {
        super(reqIp, takeUpTimestamp);
        this.topic = topic;
        this.tag = tag;
        this.reqTimestamp = reqTimestamp;
        this.msgId = msgId;
        this.mqContent = mqContent;
    }

    public static TraceMqModel create(String reqIp, String topic, String tag, String msgId, String mqContent) {
        return new TraceMqModel(reqIp, -1L, topic, tag, DateUtils.nowStr3(), msgId, mqContent);
    }

    public static TraceMqModel create(String reqIp, long takeUpTimestamp, String msgId) {
        return new TraceMqModel(reqIp, takeUpTimestamp, null, null, DateUtils.nowStr3(), msgId, null);
    }
}