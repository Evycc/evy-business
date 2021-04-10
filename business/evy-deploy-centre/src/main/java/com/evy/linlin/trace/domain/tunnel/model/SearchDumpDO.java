package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2021/4/5 23:01
 */
public class SearchDumpDO {
    private String targetIp;
    private Long threadId;
    private String code;

    public SearchDumpDO() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SearchDumpDO{" +
                "targetIp='" + targetIp + '\'' +
                ", threadId='" + threadId + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
