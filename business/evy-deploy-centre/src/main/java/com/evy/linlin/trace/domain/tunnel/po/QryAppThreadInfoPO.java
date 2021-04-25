package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:33
 */
public class QryAppThreadInfoPO {
    private final String appIp;
    private final String threadName;

    public QryAppThreadInfoPO(String appIp, String threadName) {
        this.appIp = appIp;
        this.threadName = threadName;
    }

    @Override
    public String toString() {
        return "QryAppThreadInfoPO{" +
                "appIp='" + appIp + '\'' +
                ", threadName='" + threadName + '\'' +
                '}';
    }

    public String getAppIp() {
        return appIp;
    }

    public String getThreadName() {
        return threadName;
    }
}
