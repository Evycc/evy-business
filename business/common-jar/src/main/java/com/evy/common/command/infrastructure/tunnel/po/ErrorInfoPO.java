package com.evy.common.command.infrastructure.tunnel.po;

/**
 * 错误码PO
 * @Author: EvyLiuu
 * @Date: 2020/9/6 1:14
 */
public class ErrorInfoPO {
    private String errorCode;
    private String errorMsg;

    @Override
    public String toString() {
        return "ErrorInfoPO{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

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
}
