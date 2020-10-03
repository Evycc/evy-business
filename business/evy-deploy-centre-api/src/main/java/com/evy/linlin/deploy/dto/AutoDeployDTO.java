package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * AutoDeployDTO
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:02
 */
@Getter
@Setter
@ToString
public class AutoDeployDTO extends InputDTO implements ValidatorDTO<AutoDeployDTO> {
    /**
     * 编译流水,用于关联编译应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    private String buildSeq;
    /**
     * 备注
     */
    @NotBlank(message = "remarks不能为空")
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
     * 是否分批部署 0 开启 1 不开启
     */
    @NotNull(message = "switchBatchDeploy不能为空")
    @Min(0)
    @Max(1)
    private Integer switchBatchDeploy;
}
