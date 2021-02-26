package com.evy.linlin.login.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/2/17 16:59
 */
@ToString
@Getter
@AllArgsConstructor
public class LoginServiceDTO extends InputDTO implements ValidatorDTO<LoginServiceDTO> {
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Length(max = 32, message = "username长度超限")
    private final String username;
    /**
     * 密码
     */
    @NotBlank(message = "password不能为空")
    @Length(max = 1024, message = "password长度超限")
    private final String password;
}
