package com.evy.linlin.login.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录信息返回DO
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:21
 */
@Getter
@ToString
@Setter
@AllArgsConstructor
public class LoginInfoOutDO {
    /**
     * 唯一用户标识
     */
    private String userSeq;
}
