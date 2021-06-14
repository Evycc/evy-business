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

    public QryHttpInfoModel() {
    }

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

    public void setAppIp(String appIp) {
        this.appIp = appIp;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public void setTakeUpTime(String takeUpTime) {
        this.takeUpTime = takeUpTime;
    }

    public void setReqTimeStamp(String reqTimeStamp) {
        this.reqTimeStamp = reqTimeStamp;
    }

    public void setReqSuccess(String reqSuccess) {
        this.reqSuccess = reqSuccess;
    }

    public void setReqInput(String reqInput) {
        this.reqInput = reqInput;
    }

    public void setReqResult(String reqResult) {
        this.reqResult = reqResult;
    }

    public void setGmtModify(String gmtModify) {
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
