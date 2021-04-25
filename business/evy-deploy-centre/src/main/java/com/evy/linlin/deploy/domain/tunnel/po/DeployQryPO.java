package com.evy.linlin.deploy.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:30
 */
public class DeployQryPO {
    private String seq;
    private String userSeq;
    private String deploySeq;

    public DeployQryPO(String seq, String userSeq, String deploySeq) {
        this.seq = seq;
        this.userSeq = userSeq;
        this.deploySeq = deploySeq;
    }

    public String getSeq() {
        return seq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getDeploySeq() {
        return deploySeq;
    }

    @Override
    public String toString() {
        return "DeployQryPO{" +
                "seq='" + seq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", deploySeq='" + deploySeq + '\'' +
                '}';
    }
}
