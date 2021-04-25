package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:39
 */
public class QryAppSlowSqlListDO {
    private String buildSeq;
    private String userSeq;

    public QryAppSlowSqlListDO(String buildSeq, String userSeq) {
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
