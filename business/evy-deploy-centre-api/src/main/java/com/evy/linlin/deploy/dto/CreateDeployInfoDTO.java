package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:56
 */
@ToString
@Getter
@AllArgsConstructor
public class CreateDeployInfoDTO extends InputDTO implements ValidatorDTO<CreateDeployInfoDTO> {
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
}
