package com.evy.linlin.trace.domain.tunnel.po;

import com.evy.common.utils.DateUtils;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:33
 */
public class QryAppThreadInfoListPO {
    private String tatiAppIp;
    private String tatiThreadId;
    private String tatiThreadName;
    private String tatiThreadStatus;
    private String tatiThreadStartMtime;
    private String tatiThreadBlockedCount;
    private String tatiThreadBlockedMtime;
    private String tatiThreadBlockedName;
    private String tatiThreadBlockedId;
    private String tatiThreadWaitedCount;
    private String tatiThreadWaitedMtime;
    private String tatiThreadMaxCount;
    private String tatiThreadStack;
    private String gmtModify;

    public QryAppThreadInfoListPO(String tatiAppIp, String tatiThreadId, String tatiThreadName, String tatiThreadStatus, String tatiThreadStartMtime, String tatiThreadBlockedCount, String tatiThreadBlockedMtime, String tatiThreadBlockedName, String tatiThreadBlockedId, String tatiThreadWaitedCount, String tatiThreadWaitedMtime, String tatiThreadMaxCount, String tatiThreadStack) {
        this.tatiAppIp = tatiAppIp;
        this.tatiThreadId = tatiThreadId;
        this.tatiThreadName = tatiThreadName;
        this.tatiThreadStatus = tatiThreadStatus;
        this.tatiThreadStartMtime = tatiThreadStartMtime;
        this.tatiThreadBlockedCount = tatiThreadBlockedCount;
        this.tatiThreadBlockedMtime = tatiThreadBlockedMtime;
        this.tatiThreadBlockedName = tatiThreadBlockedName;
        this.tatiThreadBlockedId = tatiThreadBlockedId;
        this.tatiThreadWaitedCount = tatiThreadWaitedCount;
        this.tatiThreadWaitedMtime = tatiThreadWaitedMtime;
        this.tatiThreadMaxCount = tatiThreadMaxCount;
        this.tatiThreadStack = tatiThreadStack;
        this.gmtModify = DateUtils.nowStr1();
    }

    public String getTatiAppIp() {
        return tatiAppIp;
    }

    public String getTatiThreadId() {
        return tatiThreadId;
    }

    public String getTatiThreadName() {
        return tatiThreadName;
    }

    public String getTatiThreadStatus() {
        return tatiThreadStatus;
    }

    public String getTatiThreadStartMtime() {
        return tatiThreadStartMtime;
    }

    public String getTatiThreadBlockedCount() {
        return tatiThreadBlockedCount;
    }

    public String getTatiThreadBlockedMtime() {
        return tatiThreadBlockedMtime;
    }

    public String getTatiThreadBlockedName() {
        return tatiThreadBlockedName;
    }

    public String getTatiThreadBlockedId() {
        return tatiThreadBlockedId;
    }

    public String getTatiThreadWaitedCount() {
        return tatiThreadWaitedCount;
    }

    public String getTatiThreadWaitedMtime() {
        return tatiThreadWaitedMtime;
    }

    public String getTatiThreadMaxCount() {
        return tatiThreadMaxCount;
    }

    public String getTatiThreadStack() {
        return tatiThreadStack;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
