package com.evy.linlin.deploy.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:42
 */
public class DeployQryOutPO {
    private String buildSeq;
    private String deploySeq;
    private String userSeq;
    private String projectName;
    private String appName;
    private String gitPath;
    private String gitBrchan;
    private String stageFlag;
    private Integer switchJunit;
    private Integer switchBatchDeploy;
    private String jarPath;
    private String jvmParam;
    private String targetHost;
    private String remarks;
    private String createDateTime;

    public String getBuildSeq() {
        return buildSeq;
    }

    public String getDeploySeq() {
        return deploySeq;
    }

    public String getUserSeq() {
        return userSeq;
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

    public String getStageFlag() {
        return stageFlag;
    }

    public Integer getSwitchJunit() {
        return switchJunit;
    }

    public Integer getSwitchBatchDeploy() {
        return switchBatchDeploy;
    }

    public String getJarPath() {
        return jarPath;
    }

    public String getJvmParam() {
        return jvmParam;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    @Override
    public String toString() {
        return "DeployQryOutPO{" +
                "buildSeq='" + buildSeq + '\'' +
                ", deploySeq='" + deploySeq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                ", projectName='" + projectName + '\'' +
                ", appName='" + appName + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", gitBrchan='" + gitBrchan + '\'' +
                ", stageFlag='" + stageFlag + '\'' +
                ", switchJunit=" + switchJunit +
                ", switchBatchDeploy=" + switchBatchDeploy +
                ", jarPath='" + jarPath + '\'' +
                ", jvmParam='" + jvmParam + '\'' +
                ", targetHost='" + targetHost + '\'' +
                ", remarks='" + remarks + '\'' +
                ", createDateTime='" + createDateTime + '\'' +
                '}';
    }
}
