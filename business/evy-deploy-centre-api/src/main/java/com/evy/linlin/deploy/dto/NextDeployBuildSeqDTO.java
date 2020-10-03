package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:21
 */
@Getter
public class NextDeployBuildSeqDTO extends InputDTO implements ValidatorDTO<NextDeployBuildSeqDTO> {
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
}
