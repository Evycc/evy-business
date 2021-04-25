package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 20:23
 */
public class QryAppMermoryInfoListDO {
    private final String seq;
    private final String userSeq;

    public QryAppMermoryInfoListDO(String seq, String userSeq) {
        this.seq = seq;
        this.userSeq = userSeq;
    }

    public String getSeq() {
        return seq;
    }

    public String getUserSeq() {
        return userSeq;
    }
}
