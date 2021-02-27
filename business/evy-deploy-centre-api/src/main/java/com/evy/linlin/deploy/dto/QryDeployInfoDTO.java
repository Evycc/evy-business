package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 查询用户名下所有部署应用信息,用于后续执行部署
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:04
 */
@ToString
@Getter
@Setter
public class QryDeployInfoDTO extends InputDTO implements ValidatorDTO<QryDeployInfoDTO> {
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
    /**
     * 部署配置标识
     */
    @Length(max = 64, message = "deploySeq长度超限")
    private String deploySeq;
}
