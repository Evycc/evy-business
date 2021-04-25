package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * SHELL返回JSON结果
 * @Author: EvyLiuu
 * @Date: 2020/9/6 13:18
 */
public class ShellOutDO {
    String errorCode;
    String msg;

    public ShellOutDO() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
