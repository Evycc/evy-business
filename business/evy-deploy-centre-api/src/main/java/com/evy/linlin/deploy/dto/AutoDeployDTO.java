package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * AutoDeployDTO
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:02
 */
public class AutoDeployDTO extends GatewayInputDTO implements ValidatorDTO<AutoDeployDTO> {
    /**
     * 编译流水,用于关联编译应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;

    public AutoDeployDTO() {
    }

    @Override
    public String toString() {
        return "AutoDeployDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                '}';
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }
}
