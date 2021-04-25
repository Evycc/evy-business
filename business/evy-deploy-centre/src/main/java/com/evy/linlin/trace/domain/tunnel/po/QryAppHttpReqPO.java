package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:43
 */
public class QryAppHttpReqPO {
    private final String appIp;
    private final Integer limit;

    public QryAppHttpReqPO(String appIp, Integer limit) {
        this.appIp = appIp;
        this.limit = limit;
    }

    public String getAppIp() {
        return appIp;
    }

    public Integer getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "QryAppHttpReqPO{" +
                "appIp='" + appIp + '\'' +
                ", limit=" + limit +
                '}';
    }
}
