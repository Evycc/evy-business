package com.evy.common.trace.infrastructure.tunnel.po;

import lombok.Getter;

/**
 * Trace Http对象PO类
 * @Author: EvyLiuu
 * @Date: 2020/6/14 17:06
 */
@Getter
public class TraceHttpPO {
    private final String thfReqIp;
    private final String thfUrl;
    private final String thfTakeUpTime;
    private final String thfReqTimestamp;
    private final boolean thfResultSuccess;
    private final String thfInput;
    private final String thfResult;

    private TraceHttpPO(String thfReqIp, String thfUrl, String thfTakeUpTime, String thfReqTimestamp, boolean thfResultSuccess, String thfInput, String thfResult) {
        this.thfReqIp = thfReqIp;
        this.thfUrl = thfUrl;
        this.thfTakeUpTime = thfTakeUpTime;
        this.thfReqTimestamp = thfReqTimestamp;
        this.thfResultSuccess = thfResultSuccess;
        this.thfInput = thfInput;
        this.thfResult = thfResult;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceHttpPO create(String thfReqIp, String thfUrl, String thfTakeUpTime, String thfReqTimestamp, boolean thfResultSuccess, String thfInput, String thfResult){
        //数据库字段长度限制
        final int limit = 1000;
        if (thfInput.length() > limit) {
            thfInput = thfInput.substring(0, limit);
        }
        if (thfResult.length() > limit) {
            thfResult = thfResult.substring(0, limit);
        }
        return new TraceHttpPO(thfReqIp, thfUrl, thfTakeUpTime, thfReqTimestamp, thfResultSuccess, thfInput, thfResult);
    }
}
