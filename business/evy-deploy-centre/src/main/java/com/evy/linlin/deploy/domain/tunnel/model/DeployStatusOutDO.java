package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 15:39
 */
public class DeployStatusOutDO {
    private final String pid;
    private final String targetHost;
    private Boolean isStart;

    public DeployStatusOutDO(String pid, String targetHost) {
        this.pid = pid;
        this.targetHost = targetHost;
        this.isStart = false;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isStart() {
        return isStart;
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
                ", isStart=" + isStart +
                '}';
    }
}
