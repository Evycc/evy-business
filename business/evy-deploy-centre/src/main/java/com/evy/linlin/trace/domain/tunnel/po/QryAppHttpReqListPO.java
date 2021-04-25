package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:44
 */
public class QryAppHttpReqListPO {
    private String thfReqIp;
    private String thfUrl;
    private String thfTakeupTime;
    private String thfReqTimestamp;
    private String thfResultSucess;
    private String thfInput;
    private String thfResult;
    private String gmtModify;

    @Override
    public String toString() {
        return "QryAppHttpReqListPO{" +
                "thfReqIp='" + thfReqIp + '\'' +
                ", thfUrl='" + thfUrl + '\'' +
                ", thfTakeupTime='" + thfTakeupTime + '\'' +
                ", thfReqTimestamp='" + thfReqTimestamp + '\'' +
                ", thfResultSucess='" + thfResultSucess + '\'' +
                ", thfInput='" + thfInput + '\'' +
                ", thfResult='" + thfResult + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }

    public String getThfReqIp() {
        return thfReqIp;
    }

    public String getThfUrl() {
        return thfUrl;
    }

    public String getThfTakeupTime() {
        return thfTakeupTime;
    }

    public String getThfReqTimestamp() {
        return thfReqTimestamp;
    }

    public String getThfResultSucess() {
        return thfResultSucess;
    }

    public String getThfInput() {
        return thfInput;
    }

    public String getThfResult() {
        return thfResult;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
