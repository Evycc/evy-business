package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 15:39
 */
public class DeployStatusOutDO {
    private String pid;
    private String targetHost;

    public DeployStatusOutDO(String pid, String targetHost) {
        this.pid = pid;
        this.targetHost = targetHost;
    }

    public String getPid() {
        return pid;
    }

    public String getTargetHost() {
        return targetHost;
    }

    @Override
    public String toString() {
        return "DeployStatusOutDO{" +
                "pid='" + pid + '\'' +
                ", targetHost='" + targetHost + '\'' +
                '}';
    }
}
