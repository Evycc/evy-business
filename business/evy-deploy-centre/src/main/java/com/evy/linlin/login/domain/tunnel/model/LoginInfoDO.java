package com.evy.linlin.login.domain.tunnel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录信息DO类
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:14
 */
@ToString
@Setter
@Getter
@NoArgsConstructor
public class LoginInfoDO {
    private String userName;
    private String password;
}
