package com.evy.common.command.infrastructure.exception;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * 异常基类，通过初始化数据库中错误码表，转换对应异常码后返回
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public class BasicException extends Exception {
    @Getter
    @Setter
    private String errorCode = ErrorConstant.ERROR_01;
    @Getter
    @Setter
    private String errorMessage;

    public BasicException(Throwable throwable) {
        super(throwable);
    }

    public BasicException(String errorCode, String errorMessage) {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    public BasicException(String errorCode) {
        setErrorCode(errorCode);
        errorMessage = BusinessConstant.EMPTY_STR;
    }

    public BasicException(String errorCode, String errorMessage, Throwable throwable) {
        super(throwable);
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    @Override
    public String toString() {
        String ss = super.toString();
        return (ss + " [" + errorCode + " : " + errorMessage + "]");
    }
}
