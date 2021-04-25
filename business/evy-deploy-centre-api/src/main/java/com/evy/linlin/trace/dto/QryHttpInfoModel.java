package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:04
 */
public class QryHttpInfoModel extends CommandModel {
    private String appIp;
    private String reqUrl;
    private String takeUpTime;
    private String reqTimeStamp;
    private String reqSuccess;
    private String reqInput;
    private String reqResult;
    private String gmtModify;

    public QryHttpInfoModel(String appIp, String reqUrl, String takeUpTime, String reqTimeStamp, String reqSuccess, String reqInput, String reqResult, String gmtModify) {
        this.appIp = appIp;
        this.reqUrl = reqUrl;
        this.takeUpTime = takeUpTime;
        this.reqTimeStamp = reqTimeStamp;
        this.reqSuccess = reqSuccess;
        this.reqInput = reqInput;
        this.reqResult = reqResult;
        this.gmtModify = gmtModify;
    }

    public String getAppIp() {
        return appIp;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public String getTakeUpTime() {
        return takeUpTime;
    }

    public String getReqTimeStamp() {
        return reqTimeStamp;
    }

    public String getReqSuccess() {
        return reqSuccess;
    }

    public String getReqInput() {
        return reqInput;
    }

    public String getReqResult() {
        return reqResult;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
