package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:01
 */
public class QryThreadsInfoDTO extends GatewayInputDTO implements ValidatorDTO<QryThreadsInfoDTO> {
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
     * 查询指定线程时用
     */
    @Length(max = 100, message = "threadName长度超限")
    private String threadName;
    /**
     * 查询指定服务器
     */
    @NotBlank(message = "serviceIp不能为空")
    @Length(max = 100, message = "serviceIp长度超限")
    private String serviceIp;
    /**
     * 忽略的记录数，由于线程信息较多，采取分段返回
     */
    @Min(value = 0, message = "beginIndex最小值0")
    private Integer beginIndex;

    /**
     * 忽略的记录数，由于线程信息较多，采取分段返回
     */
    @Min(value = 10, message = "endIndex最小值10")
    private Integer endIndex;

    @Override
    public String toString() {
        return "QryThreadsInfoDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", threadName='" + threadName + '\'' +
                ", serviceIp='" + serviceIp + '\'' +
                ", beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                '}';
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public void setBeginIndex(Integer beginIndex) {
        this.beginIndex = beginIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public Integer getBeginIndex() {
        return beginIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }
}
