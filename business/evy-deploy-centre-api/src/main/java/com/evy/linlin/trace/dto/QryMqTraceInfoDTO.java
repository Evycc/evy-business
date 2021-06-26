package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:46
 */
public class QryMqTraceInfoDTO extends GatewayInputDTO implements ValidatorDTO<QryMqTraceInfoDTO> {
    /**
     * 编译流水,用于关联编译应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
    /**
     * 查询topic
     */
    private String topic;
    /**
     * 查询消息msgId
     */
    private String msgId;
    /**
     * 查询记录数
     */
    private Integer limit;

    @Override
    public String toString() {
        return "QryMqTraceInfoDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", topic='" + topic + '\'' +
                ", msgId='" + msgId + '\'' +
                ", limit=" + limit +
                '}';
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getBuildSeq() {
        return buildSeq;
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
