package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 19:53
 */
@ToString
@AllArgsConstructor
@Getter
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
}
