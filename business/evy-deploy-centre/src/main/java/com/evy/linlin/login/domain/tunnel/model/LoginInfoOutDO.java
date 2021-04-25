package com.evy.linlin.login.domain.tunnel.model;

/**
 * 登录信息返回DO
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:21
 */
public class LoginInfoOutDO {
    /**
     * 唯一用户标识
     */
    private String userSeq;

    public LoginInfoOutDO(String userSeq) {
        this.userSeq = userSeq;
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }

    @Override
    public String toString() {
        return "LoginInfoOutDO{" +
                "userSeq='" + userSeq + '\'' +
                '}';
    }
}
