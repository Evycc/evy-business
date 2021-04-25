package com.evy.linlin.trace.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 0:07
 */
public class QryAppServiceInfoListDO {
    private String buildSeq;
    private String userSeq;
    private String serviceName;

    public QryAppServiceInfoListDO(String buildSeq, String userSeq, String serviceName) {
        this.buildSeq = buildSeq;
        this.userSeq = userSeq;
        this.serviceName = serviceName;
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public String getServiceName() {
        return serviceName;
    }
}
