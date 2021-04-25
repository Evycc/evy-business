package com.evy.common.trace.infrastructure.tunnel.model;

import com.evy.common.utils.DateUtils;

/**
 * Http Trace Model
 * @Author: EvyLiuu
 * @Date: 2020/6/7 20:30
 */
public class TraceHttpModel extends TraceModel {
    private String reqUrl;
    private String reqTimestamp;
    private boolean resultSuccess;
    private String reqParam;
    private String respResult;

    private TraceHttpModel(String reqIp, long takeUpTimestamp, String reqUrl, String reqTimestamp, boolean resultSuccess, String reqParam, String respResult) {
        super(reqIp, takeUpTimestamp);
        this.reqUrl = reqUrl;
        this.reqTimestamp = reqTimestamp;
        this.resultSuccess = resultSuccess;
        this.reqParam = reqParam;
        this.respResult = respResult;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceHttpModel create(String reqIp, long takeUpTimestamp, String reqUrl, boolean resultSuccess, String reqParam, String respResult) {
        return new TraceHttpModel(reqIp, takeUpTimestamp, reqUrl, DateUtils.nowStr1(), resultSuccess, reqParam, respResult);
    }

    @Override
    public String toString() {
        return "TraceHttpModel{" +
                "reqUrl='" + reqUrl + '\'' +
                ", reqTimestamp='" + reqTimestamp + '\'' +
                ", resultSuccess=" + resultSuccess +
                ", reqParam='" + reqParam + '\'' +
                ", respResult='" + respResult + '\'' +
                '}';
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public String getReqTimestamp() {
        return reqTimestamp;
    }

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    public String getReqParam() {
        return reqParam;
    }

    public String getRespResult() {
        return respResult;
    }
}
