package com.evy.common.command.infrastructure.tunnel.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 接口入参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
public class InputDTO implements Serializable {
    private static final long serialVersionUID = 546422L;
    @NotNull(message = "srcSendNo不能为空")
    private String srcSendNo;
    @NotNull(message = "requestTime不能为空")
    private String requestTime;
    @NotNull(message = "clientIp不能为空")
    private String clientIp;
    private String traceId;

    public String getSrcSendNo() {
        return srcSendNo;
    }

    public void setSrcSendNo(String srcSendNo) {
        this.srcSendNo = srcSendNo;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "InputDTO{" +
                "srcSendNo='" + srcSendNo + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
