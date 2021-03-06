package com.evy.common.command.infrastructure.exception;

import com.evy.common.command.domain.factory.ErrorFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;

/**
 * 异常基类，通过初始化数据库中错误码表，转换对应异常码后返回
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public class BasicException extends Exception {
    private String errorCode = ErrorConstant.ERROR_01;
    private String errorMessage;

    public BasicException(Throwable throwable) {
        super(throwable);
    }

    public BasicException(String errorCode, String errorMessage) {
        super(errorCode + BusinessConstant.COLON_STR + errorMessage);
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    public BasicException(String errorCode) {
        super(errorCode);
        setErrorCode(errorCode);
        ErrorFactory.handleErrorCode(this);
    }

    public BasicException(String errorCode, String errorMessage, Throwable throwable) {
        super(throwable);
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        String ss = super.toString();
        return (ss + " [" + errorCode + " : " + errorMessage + "]");
    }
}
