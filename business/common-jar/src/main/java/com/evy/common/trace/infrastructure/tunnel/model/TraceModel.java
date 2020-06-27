package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * Trace Model
 * @Author: EvyLiuu
 * @Date: 2020/6/7 20:58
 */
public class TraceModel {
    private String reqIp;
    private long takeUpTimestamp;

    public TraceModel(String reqIp, long takeUpTimestamp) {
        this.reqIp = reqIp;
        this.takeUpTimestamp = takeUpTimestamp;
    }

    public String getReqIp() {
        return reqIp;
    }

    public long getTakeUpTimestamp() {
        return takeUpTimestamp;
    }
}
