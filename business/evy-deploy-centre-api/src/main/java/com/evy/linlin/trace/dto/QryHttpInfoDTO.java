package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:45
 */
@Getter
@ToString
public class QryHttpInfoDTO extends InputDTO implements ValidatorDTO<QryHttpInfoDTO> {
    /**
     * 编译流水,用于关联编译应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
    /**
     * 请求路径,模糊查询
     */
    @Length(max = 1000, message = "userSeq长度超限")
    private String path;
    /**
     * 查询记录数
     */
    @Min(1)
    @Max(100)
    private Integer limit;
}
