package com.evy.linlin.trace.dto;

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
public class QryMqTraceInfoModel {
    private String topic;
    private String reqIp;
    private String tag;
    private String msgId;
    private String mqContent;
    private String respIp;
    private String startTimestamp;
    private String endTimestamp;
    private String takeUpTimestamp;
    private String gmtModify;
}
