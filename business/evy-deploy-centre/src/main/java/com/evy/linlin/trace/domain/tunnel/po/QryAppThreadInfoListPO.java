package com.evy.linlin.trace.domain.tunnel.po;

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
