package com.evy.linlin.trace.domain.tunnel.po;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:41
 */
public class QryAppSlowSqlPO {
    private final List<String> appIps;

    public QryAppSlowSqlPO(List<String> appIps) {
        this.appIps = appIps;
    }

    public List<String> getAppIps() {
        return appIps;
    }

    @Override
    public String toString() {
        return "QryAppSlowSqlPO{" +
                "appIps=" + appIps +
                '}';
    }
}
