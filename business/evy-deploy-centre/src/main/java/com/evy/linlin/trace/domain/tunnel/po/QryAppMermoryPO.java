package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:11
 */
public class QryAppMermoryPO {
    private final String appIp;

    public QryAppMermoryPO(String appIp) {
        this.appIp = appIp;
    }

    public String getAppIp() {
        return appIp;
    }

    @Override
    public String toString() {
        return "QryAppMermoryPO{" +
                "appIp='" + appIp + '\'' +
                '}';
    }
}
