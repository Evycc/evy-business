package com.evy.common.command.infrastructure.tunnel.dto;

import com.evy.common.command.infrastructure.constant.ErrorConstant;

import java.io.Serializable;

/**
 * 接口出参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
public class OutDTO implements Serializable {
    private static final long serialVersionUID = 546422L;
    private String errorCode = ErrorConstant.SUCCESS;
    private String errorMsg = "交易成功";

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "OutDTO{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
