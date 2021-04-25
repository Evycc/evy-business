package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:58
 */
public class QryAppThreadInfoListDO {
    private String buildSeq;
    private String userSeq;
    private String threadName;
    private Integer beginIndex;
    private Integer endIndex;
    private String serviceIp;

    public QryAppThreadInfoListDO(String buildSeq, String userSeq, String threadName, Integer beginIndex, Integer endIndex, String serviceIp) {
        this.buildSeq = buildSeq;
        this.userSeq = userSeq;
        this.threadName = threadName;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.serviceIp = serviceIp;
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

    public Integer getBeginIndex() {
        return beginIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public String getServiceIp() {
        return serviceIp;
    }
}
