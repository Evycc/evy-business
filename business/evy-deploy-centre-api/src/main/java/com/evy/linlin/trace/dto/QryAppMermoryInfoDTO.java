package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:44
 */
public class QryAppMermoryInfoDTO extends InputDTO implements ValidatorDTO<QryAppMermoryInfoDTO> {
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

    @Override
    public String toString() {
        return "QryAppMermoryInfoDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                ", userSeq='" + userSeq + '\'' +
                '}';
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }
}
