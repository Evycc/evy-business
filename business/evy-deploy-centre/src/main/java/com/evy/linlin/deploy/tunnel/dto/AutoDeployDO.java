package com.evy.linlin.deploy.tunnel.dto;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
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
    /**
     * 是否分批部署
     */
    private boolean switchBatchDeploy;

    public static AutoDeployDO convertFromDto(AutoDeployDTO deployDTO) {
        boolean isSwitchJunit = deployDTO.getSwitchJunit() == BusinessConstant.ZERO_NUM;
        boolean isSwitchBatchDeploy = deployDTO.getSwitchBatchDeploy() == BusinessConstant.ZERO_NUM;
        return new AutoDeployDO(deployDTO.getGitPath(), deployDTO.getProjectName(), deployDTO.getBrchanName(),
                deployDTO.getAppName(), deployDTO.getRemarks(), deployDTO.getJvmParam(), isSwitchJunit,
                deployDTO.getTargetHost(), isSwitchBatchDeploy);
    }
}
