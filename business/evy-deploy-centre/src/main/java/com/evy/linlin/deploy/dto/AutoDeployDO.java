package com.evy.linlin.deploy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:09
 */
@Getter
@Setter
@AllArgsConstructor
public class AutoDeployDO {
    /**
     * git路径
     */
    private String gitPath;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * git分支
     */
    private String brchanName;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 备注
     */
    private String remarks;
    /**
     * jvm参数
     */
    private String jvmParam;
    /**
     * 是否开启junit
     */
    private boolean switchJunit;
    /**
     * 部署目标服务器
     */
    private String targetHost;

    public static AutoDeployDO convertFromDto(AutoDeployDTO deployDTO) {
        return new AutoDeployDO(deployDTO.getGitPath(), deployDTO.getProjectName(), deployDTO.getBrchanName(),
                deployDTO.getAppName(), deployDTO.getRemarks(), deployDTO.getJvmParam(), deployDTO.isSwitchJunit(),
                deployDTO.getTargetHost());
    }
}
