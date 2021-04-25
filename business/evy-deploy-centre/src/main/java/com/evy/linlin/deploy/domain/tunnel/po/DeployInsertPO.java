package com.evy.linlin.deploy.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:23
 */
public class DeployInsertPO {
    private String seq;
    private String userSeq;
    private String deploySeq;
    private String projectName;
    private String appName;
    private String gitPath;
    private String gitBrchan;
    private String remarks;
    private String jvmParam;
    private String targetHost;
    private Integer switchBatchDeploy;
    private Integer switchJunit;

    public DeployInsertPO(String seq, String userSeq, String deploySeq, String projectName, String appName, String gitPath, String gitBrchan, String remarks, String jvmParam, String targetHost, Integer switchBatchDeploy, Integer switchJunit) {
        this.seq = seq;
        this.userSeq = userSeq;
        this.deploySeq = deploySeq;
        this.projectName = projectName;
        this.appName = appName;
        this.gitPath = gitPath;
        this.gitBrchan = gitBrchan;
        this.remarks = remarks;
        this.jvmParam = jvmParam;
        this.targetHost = targetHost;
        this.switchBatchDeploy = switchBatchDeploy;
        this.switchJunit = switchJunit;
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

    public String getProjectName() {
        return projectName;
    }

    public String getAppName() {
        return appName;
    }

    public String getGitPath() {
        return gitPath;
    }

    public String getGitBrchan() {
        return gitBrchan;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getJvmParam() {
        return jvmParam;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public Integer getSwitchBatchDeploy() {
        return switchBatchDeploy;
    }

    public Integer getSwitchJunit() {
        return switchJunit;
    }

    @Override
    public String toString() {
        return "DeployInsertPO{" +
                "seq='" + seq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", deploySeq='" + deploySeq + '\'' +
                ", projectName='" + projectName + '\'' +
                ", appName='" + appName + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", gitBrchan='" + gitBrchan + '\'' +
                ", remarks='" + remarks + '\'' +
                ", jvmParam='" + jvmParam + '\'' +
                ", targetHost='" + targetHost + '\'' +
                ", switchBatchDeploy=" + switchBatchDeploy +
                ", switchJunit=" + switchJunit +
                '}';
    }
}
