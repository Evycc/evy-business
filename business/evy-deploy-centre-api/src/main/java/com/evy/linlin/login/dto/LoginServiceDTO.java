package com.evy.linlin.login.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.linlin.gateway.GatewayInputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2021/2/17 16:59
 */
public class LoginServiceDTO extends GatewayInputDTO implements ValidatorDTO<LoginServiceDTO> {
    /**
     * 用户名
     */
    @NotBlank(message = "username不能为空")
    @Length(max = 32, message = "username长度超限")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "password不能为空")
    @Length(max = 1024, message = "password长度超限")
    private String password;

    public LoginServiceDTO() {
    }

    public LoginServiceDTO(@NotBlank(message = "username不能为空") @Length(max = 32, message = "username长度超限") String username, @NotBlank(message = "password不能为空") @Length(max = 1024, message = "password长度超限") String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginServiceDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
