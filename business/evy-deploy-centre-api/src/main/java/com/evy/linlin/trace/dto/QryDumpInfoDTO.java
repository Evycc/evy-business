package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/4/5 22:28
 */
public class QryDumpInfoDTO extends GatewayInputDTO implements ValidatorDTO<QryDumpInfoDTO> {
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
     * 目标服务器IP
     */
    @NotBlank(message = "targetIp不能为空")
    @Length(max = 100, message = "targetIp长度超限")
    private String targetIp;
    /**
     * 线程ID
     */
    private Long threadId;
    /**
     * 内部使用,调用的方法
     */
    private String code;

    public QryDumpInfoDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    @Override
    public String toString() {
        return "QryDumpInfoDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", threadId=" + threadId +
                '}';
    }
}
