package com.evy.linlin.trace.domain.tunnel.po;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:30
 */
public class QryServiceInfoPO {
    private final List<String> appIps;
    private final String serviceBeanName;

    public QryServiceInfoPO(List<String> appIps, String serviceBeanName) {
        this.appIps = appIps;
        this.serviceBeanName = serviceBeanName;
    }

    public List<String> getAppIps() {
        return appIps;
    }

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    @Override
    public String toString() {
        return "QryServiceInfoPO{" +
                "appIps=" + appIps +
                ", serviceBeanName='" + serviceBeanName + '\'' +
                '}';
    }
}
