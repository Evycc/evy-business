package com.evy.linlin.login.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2021/2/17 16:58
 */
public class LoginServiceOutDTO extends GatewayOutDTO {
    /**
     * 唯一用户标识,以后通过该标识关联部署信息
     */
    private String userSeq;

    public LoginServiceOutDTO() {
    }

    public LoginServiceOutDTO(String userSeq) {
        this.userSeq = userSeq;
    }

    @Override
    public String toString() {
        return "LoginServiceOutDTO{" +
                "userSeq='" + userSeq + '\'' +
                '}';
    }

    public String getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(String userSeq) {
        this.userSeq = userSeq;
    }
}
