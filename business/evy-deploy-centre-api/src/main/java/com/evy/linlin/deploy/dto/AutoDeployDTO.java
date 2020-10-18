package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

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
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;
}
