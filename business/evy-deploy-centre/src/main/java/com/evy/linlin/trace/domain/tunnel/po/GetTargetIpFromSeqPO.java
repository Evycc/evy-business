package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:43
 */
public class GetTargetIpFromSeqPO {
    private final String seq;
    private final String userSeq;

    public GetTargetIpFromSeqPO(String seq, String userSeq) {
        this.seq = seq;
        this.userSeq = userSeq;
    }

    @Override
    public String toString() {
        return "GetTargetIpFromSeqPO{" +
                "seq='" + seq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                '}';
    }

    public String getSeq() {
        return seq;
    }

    public String getUserSeq() {
        return userSeq;
    }
}
