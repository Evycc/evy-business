package com.evy.linlin.login.domain.tunnel.model;

/**
 * 登录信息DO类
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:14
 */
public class LoginInfoDO {
    private String userName;
    private String password;

    public LoginInfoDO() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginInfoDO{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
