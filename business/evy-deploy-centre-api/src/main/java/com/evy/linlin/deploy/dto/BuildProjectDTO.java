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
 * @Author: EvyLiuu
 * @Date: 2020/9/26 10:59
 */
@Getter
@Setter
@ToString
public class BuildProjectDTO extends InputDTO implements ValidatorDTO<BuildProjectDTO> {
    /**
     * 是否开启junit 0 开启 1 不开启
     */
    @NotNull(message = "switchJunit不能为空")
    @Min(0)
    @Max(1)
    private Integer switchJunit;
    /**
     * 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;
}
