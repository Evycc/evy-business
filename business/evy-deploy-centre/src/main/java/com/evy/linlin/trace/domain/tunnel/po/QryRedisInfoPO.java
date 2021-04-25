package com.evy.linlin.trace.domain.tunnel.po;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:17
 */
public class QryRedisInfoPO {
    private final List<String> appIps;

    public QryRedisInfoPO(List<String> appIps) {
        this.appIps = appIps;
    }

    public List<String> getAppIps() {
        return appIps;
    }

    @Override
    public String toString() {
        return "QryRedisInfoPO{" +
                "appIps=" + appIps +
                '}';
    }
}
