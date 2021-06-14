package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 19:53
 */
public class QryMqTraceInfoModel extends CommandModel {
    /**
     * 消息topic
     */
    private String topic;
    /**
     * 发送方IP
     */
    private String reqIp;
    /**
     * 消息tag
     */
    private String tag;
    /**
     * 消息msgId
     */
    private String msgId;
    /**
     * 消息正文
     */
    private String mqContent;
    /**
     * 消费方IP
     */
    private String respIp;
    /**
     * 发生时间戳
     */
    private String startTimestamp;
    /**
     * 消费时间戳
     */
    private String endTimestamp;
    /**
     * 消费耗时
     */
    private String takeUpTimestamp;
    /**
     * 消息记录时间戳
     */
    private String gmtModify;

    public QryMqTraceInfoModel() {
    }

    public QryMqTraceInfoModel(String topic, String reqIp, String tag, String msgId, String mqContent, String respIp, String startTimestamp, String endTimestamp, String takeUpTimestamp, String gmtModify) {
        this.topic = topic;
        this.reqIp = reqIp;
        this.tag = tag;
        this.msgId = msgId;
        this.mqContent = mqContent;
        this.respIp = respIp;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.takeUpTimestamp = takeUpTimestamp;
        this.gmtModify = gmtModify;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setMqContent(String mqContent) {
        this.mqContent = mqContent;
    }

    public void setRespIp(String respIp) {
        this.respIp = respIp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setTakeUpTimestamp(String takeUpTimestamp) {
        this.takeUpTimestamp = takeUpTimestamp;
    }

    public void setGmtModify(String gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getTopic() {
        return topic;
    }

    public String getReqIp() {
        return reqIp;
    }

    public String getTag() {
        return tag;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getMqContent() {
        return mqContent;
    }

    public String getRespIp() {
        return respIp;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public String getTakeUpTimestamp() {
        return takeUpTimestamp;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    @Override
    public String toString() {
        return "QryMqTraceInfoModel{" +
                "topic='" + topic + '\'' +
                ", reqIp='" + reqIp + '\'' +
                ", tag='" + tag + '\'' +
                ", msgId='" + msgId + '\'' +
                ", mqContent='" + mqContent + '\'' +
                ", respIp='" + respIp + '\'' +
                ", startTimestamp='" + startTimestamp + '\'' +
                ", endTimestamp='" + endTimestamp + '\'' +
                ", takeUpTimestamp='" + takeUpTimestamp + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }
}
