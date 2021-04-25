package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 21:41
 */
public class QryHttpReqInfoListDO {
    private final String seq;
    private final String userSeq;
    private final String path;
    private final Integer limit;

    public QryHttpReqInfoListDO(String seq, String userSeq, String path, Integer limit) {
        this.seq = seq;
        this.userSeq = userSeq;
        this.path = path;
        this.limit = limit;
    }

    public String getSeq() {
        return seq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getPath() {
        return path;
    }

    public Integer getLimit() {
        return limit;
    }
}
