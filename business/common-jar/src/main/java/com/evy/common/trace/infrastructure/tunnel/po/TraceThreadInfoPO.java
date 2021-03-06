package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import org.springframework.util.StringUtils;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/5 10:48
 */
public class TraceThreadInfoPO {
    private final String tatiAppIp;
    private final int tatiThreadId;
    private final String tatiThreadName;
    private final String tatiThreadStatus;
    private final String tatiThreadAvailByte;
    private final String tatiThreadStartMtime;
    private final int tatiThreadBlockedCount;
    private final String tatiThreadBlockedMtime;
    private final String tatiThreadBlockedName;
    private final int tatiThreadBlockedId;
    private final int tatiThreadWaitedCount;
    private final String tatiThreadWaitedMtime;
    private final int tatiThreadMaxCount;
    private final String tatiThreadStack;

    private TraceThreadInfoPO(String tatiAppIp, int tatiThreadId, String tatiThreadName, String tatiThreadStatus, String tatiThreadAvailByte, String tatiThreadStartMtime, int tatiThreadBlockedCount, String tatiThreadBlockedMtime, String tatiThreadBlockedName, int tatiThreadBlockedId, int tatiThreadWaitedCount, String tatiThreadWaitedMtime, int tatiThreadMaxCount, String tatiThreadStack) {
        this.tatiAppIp = tatiAppIp;
        this.tatiThreadId = tatiThreadId;
        this.tatiThreadName = tatiThreadName;
        this.tatiThreadStatus = tatiThreadStatus;
        this.tatiThreadAvailByte = tatiThreadAvailByte;
        this.tatiThreadStartMtime = tatiThreadStartMtime;
        this.tatiThreadBlockedCount = tatiThreadBlockedCount;
        this.tatiThreadBlockedMtime = tatiThreadBlockedMtime;
        this.tatiThreadBlockedName = tatiThreadBlockedName;
        this.tatiThreadBlockedId = tatiThreadBlockedId;
        this.tatiThreadWaitedCount = tatiThreadWaitedCount;
        this.tatiThreadWaitedMtime = tatiThreadWaitedMtime;
        this.tatiThreadMaxCount = tatiThreadMaxCount;
        this.tatiThreadStack = tatiThreadStack;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceThreadInfoPO create(int tatiThreadId, String tatiThreadName, String tatiThreadStatus, String tatiThreadAvailByte, String tatiThreadStartMtime, int tatiThreadBlockedCount, String tatiThreadBlockedMtime, String tatiThreadBlockedName, int tatiThreadBlockedId, int tatiThreadWaitedCount, String tatiThreadWaitedMtime, int tatiThreadMaxCount, String tatiThreadStack) {
        if (!StringUtils.isEmpty(tatiThreadBlockedMtime)) {
            long tempBlockedTime = Long.parseLong(tatiThreadBlockedMtime);
            long limit = 1000L;
            if (tempBlockedTime > limit) {
                tatiThreadBlockedMtime = String.valueOf(tempBlockedTime / limit);
            }
        }
        return new TraceThreadInfoPO(BusinessConstant.VM_HOST, tatiThreadId, tatiThreadName, tatiThreadStatus, tatiThreadAvailByte, tatiThreadStartMtime, tatiThreadBlockedCount, tatiThreadBlockedMtime, tatiThreadBlockedName, tatiThreadBlockedId, tatiThreadWaitedCount, tatiThreadWaitedMtime, tatiThreadMaxCount, tatiThreadStack);
    }

    public String getTatiAppIp() {
        return tatiAppIp;
    }

    public int getTatiThreadId() {
        return tatiThreadId;
    }

    public String getTatiThreadName() {
        return tatiThreadName;
    }

    public String getTatiThreadStatus() {
        return tatiThreadStatus;
    }

    public String getTatiThreadAvailByte() {
        return tatiThreadAvailByte;
    }

    public String getTatiThreadStartMtime() {
        return tatiThreadStartMtime;
    }

    public int getTatiThreadBlockedCount() {
        return tatiThreadBlockedCount;
    }

    public String getTatiThreadBlockedMtime() {
        return tatiThreadBlockedMtime;
    }

    public String getTatiThreadBlockedName() {
        return tatiThreadBlockedName;
    }

    public int getTatiThreadBlockedId() {
        return tatiThreadBlockedId;
    }

    public int getTatiThreadWaitedCount() {
        return tatiThreadWaitedCount;
    }

    public String getTatiThreadWaitedMtime() {
        return tatiThreadWaitedMtime;
    }

    public int getTatiThreadMaxCount() {
        return tatiThreadMaxCount;
    }

    public String getTatiThreadStack() {
        return tatiThreadStack;
    }
}
