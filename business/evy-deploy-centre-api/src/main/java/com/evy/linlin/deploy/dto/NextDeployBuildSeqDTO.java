package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:21
 */
public class NextDeployBuildSeqDTO extends GatewayInputDTO implements ValidatorDTO<NextDeployBuildSeqDTO> {
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
    /**
     * 部署配置标识
     */
    @NotBlank(message = "deploySeq不能为空")
    @Length(max = 64, message = "deploySeq长度超限")
    private String deploySeq;
    /**
     * git路径
     */
    @NotBlank(message = "gitPath不能为空")
    @Length(max = 1024, message = "gitPath长度超限")
    private String gitPath;
    /**
     * git分支
     */
    @NotBlank(message = "brchanName不能为空")
    @Length(max = 100)
    private String brchanName;
    /**
     * 应用名称
     */
    @NotBlank(message = "appName不能为空")
    @Length(max = 100)
    private String appName;
    /**
     * 备注
     */
    @Length(max = 100)
    private String remarks;
    /**
     * jvm参数
     */
    @Length(max = 1024)
    private String jvmParam;
    /**
     * 部署目标服务器host,允许多个,用 | 分割
     */
    @NotBlank(message = "targetHost不能为空")
    @Length(max = 100)
    private String targetHost;
    /**
     * 是否分批部署 true 开启 false 不开启
     */
    @NotNull(message = "switchBatchDeploy不能为空")
    private Boolean switchBatchDeploy;
    /**
     * 是否开启Junit true 开启 false 不开启
     */
    @NotNull(message = "switchJunit不能为空")
    private Boolean switchJunit;

    public NextDeployBuildSeqDTO() {
    }

    @Override
    public String toString() {
        return "NextDeployBuildSeqDTO{" +
                "userSeq='" + userSeq + '\'' +
                ", deploySeq='" + deploySeq + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", brchanName='" + brchanName + '\'' +
                ", appName='" + appName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", jvmParam='" + jvmParam + '\'' +
                ", targetHost='" + targetHost + '\'' +
                ", switchBatchDeploy=" + switchBatchDeploy +
                ", switchJunit=" + switchJunit +
                '}';
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getDeploySeq() {
        return deploySeq;
    }

    public void setDeploySeq(String deploySeq) {
        this.deploySeq = deploySeq;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getBrchanName() {
        return brchanName;
    }

    public void setBrchanName(String brchanName) {
        this.brchanName = brchanName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public Boolean getSwitchBatchDeploy() {
        return switchBatchDeploy;
    }

    public void setSwitchBatchDeploy(Boolean switchBatchDeploy) {
        this.switchBatchDeploy = switchBatchDeploy;
    }

    public Boolean getSwitchJunit() {
        return switchJunit;
    }

    public void setSwitchJunit(Boolean switchJunit) {
        this.switchJunit = switchJunit;
    }
}
