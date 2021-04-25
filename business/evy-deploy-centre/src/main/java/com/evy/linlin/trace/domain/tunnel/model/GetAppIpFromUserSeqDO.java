package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:28
 */
public class GetAppIpFromUserSeqDO {
    private final String seq;
    private final String userSeq;

    public GetAppIpFromUserSeqDO(String seq, String userSeq) {
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
