package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:31
 */
public class QryRedisInfoDO {
    private String buildSeq;
    private String userSeq;

    public QryRedisInfoDO(String buildSeq, String userSeq) {
        this.buildSeq = buildSeq;
        this.userSeq = userSeq;
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }
}
