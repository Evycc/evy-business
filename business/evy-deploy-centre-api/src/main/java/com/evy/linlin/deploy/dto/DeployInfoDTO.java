package com.evy.linlin.deploy.dto;

/**
 * 部署应用信息
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:09
 */
public class DeployInfoDTO {
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

    public DeployInfoDTO() {
    }

    public DeployInfoDTO(String buildSeq, String deploySeq, String userSeq, String projectName, String appName, String gitPath, String gitBrchan, String stageFlag, Integer switchJunit, Integer switchBatchDeploy, String jarPath, String jvmParam, String targetHost, String remarks, String createDateTime) {
        this.buildSeq = buildSeq;
        this.deploySeq = deploySeq;
        this.userSeq = userSeq;
        this.projectName = projectName;
        this.appName = appName;
        this.gitPath = gitPath;
        this.gitBrchan = gitBrchan;
        this.stageFlag = stageFlag;
        this.switchJunit = switchJunit;
        this.switchBatchDeploy = switchBatchDeploy;
        this.jarPath = jarPath;
        this.jvmParam = jvmParam;
        this.targetHost = targetHost;
        this.remarks = remarks;
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return "DeployInfoDTO{" +
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

    public String getBuildSeq() {
        return buildSeq;
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public String getDeploySeq() {
        return deploySeq;
    }

    public void setDeploySeq(String deploySeq) {
        this.deploySeq = deploySeq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getGitBrchan() {
        return gitBrchan;
    }

    public void setGitBrchan(String gitBrchan) {
        this.gitBrchan = gitBrchan;
    }

    public String getStageFlag() {
        return stageFlag;
    }

    public void setStageFlag(String stageFlag) {
        this.stageFlag = stageFlag;
    }

    public Integer getSwitchJunit() {
        return switchJunit;
    }

    public void setSwitchJunit(Integer switchJunit) {
        this.switchJunit = switchJunit;
    }

    public Integer getSwitchBatchDeploy() {
        return switchBatchDeploy;
    }

    public void setSwitchBatchDeploy(Integer switchBatchDeploy) {
        this.switchBatchDeploy = switchBatchDeploy;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getJvmParam() {
        return jvmParam;
    }

    public void setJvmParam(String jvmParam) {
        this.jvmParam = jvmParam;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }
}
