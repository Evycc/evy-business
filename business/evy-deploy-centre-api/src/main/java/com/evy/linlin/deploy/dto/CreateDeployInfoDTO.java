package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:56
 */
public class CreateDeployInfoDTO extends GatewayInputDTO implements ValidatorDTO<CreateDeployInfoDTO> {
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
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
     * 是否分批部署 0 开启 1 不开启
     */
    @NotNull(message = "switchBatchDeploy不能为空")
    @Min(0)
    @Max(1)
    private Integer switchBatchDeploy;
    /**
     * 是否开启Junit 0 开启 1 不开启
     */
    @NotNull(message = "switchJunit不能为空")
    @Min(0)
    @Max(1)
    private Integer switchJunit;

    public CreateDeployInfoDTO() {
    }

    public CreateDeployInfoDTO(@NotBlank(message = "userSeq不能为空") @Length(max = 64, message = "userSeq长度超限") String userSeq, @NotBlank(message = "gitPath不能为空") @Length(max = 1024, message = "gitPath长度超限") String gitPath, @NotBlank(message = "brchanName不能为空") @Length(max = 100) String brchanName, @NotBlank(message = "appName不能为空") @Length(max = 100) String appName, @Length(max = 1024) String jvmParam, @NotBlank(message = "targetHost不能为空") @Length(max = 100) String targetHost, @NotNull(message = "switchBatchDeploy不能为空") @Min(0) @Max(1) Integer switchBatchDeploy, @NotNull(message = "switchJunit不能为空") @Min(0) @Max(1) Integer switchJunit) {
        this.userSeq = userSeq;
        this.gitPath = gitPath;
        this.brchanName = brchanName;
        this.appName = appName;
        this.jvmParam = jvmParam;
        this.targetHost = targetHost;
        this.switchBatchDeploy = switchBatchDeploy;
        this.switchJunit = switchJunit;
    }

    @Override
    public String toString() {
        return "CreateDeployInfoDTO{" +
                "userSeq='" + userSeq + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", brchanName='" + brchanName + '\'' +
                ", appName='" + appName + '\'' +
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

    public Integer getSwitchBatchDeploy() {
        return switchBatchDeploy;
    }

    public void setSwitchBatchDeploy(Integer switchBatchDeploy) {
        this.switchBatchDeploy = switchBatchDeploy;
    }

    public Integer getSwitchJunit() {
        return switchJunit;
    }

    public void setSwitchJunit(Integer switchJunit) {
        this.switchJunit = switchJunit;
    }
}
