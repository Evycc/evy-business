package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:33
 */
@Getter
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
}
