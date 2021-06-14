package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:01
 */
public class QryThreadsInfoModel extends CommandModel {
    private String appIp;
    private String threadId;
    private String threadName;
    private String threadStatus;
    /**
     * 线程运行时间 单位:毫秒
     */
    private String threadStartTimeMs;
    /**
     * 死锁阻止进入线程的总数
     */
    private String threadBlockedCount;
    /**
     * 死锁累计运行时间 单位:毫秒
     */
    private String threadBlockedTimeMs;
    /**
     * 等待的死锁线程名
     */
    private String threadBlockedName;
    /**
     * 等待的死锁线程id
     */
    private String threadBlockedId;
    /**
     * 线程等待总数
     */
    private String threadWaitedCount;
    /**
     * 线程等待时间 单位:毫秒
     */
    private String threadWaitedTimeMs;
    /**
     * JVM启动以来创建的线程总数
     */
    private String threadMaxCount;
    private String threadStack;
    private String gmtModify;

    public QryThreadsInfoModel() {
    }

    public QryThreadsInfoModel(String appIp, String threadId, String threadName, String threadStatus, String threadStartTimeMs, String threadBlockedCount, String threadBlockedTimeMs, String threadBlockedName, String threadBlockedId, String threadWaitedCount, String threadWaitedTimeMs, String threadMaxCount, String threadStack, String gmtModify) {
        this.appIp = appIp;
        this.threadId = threadId;
        this.threadName = threadName;
        this.threadStatus = threadStatus;
        this.threadStartTimeMs = threadStartTimeMs;
        this.threadBlockedCount = threadBlockedCount;
        this.threadBlockedTimeMs = threadBlockedTimeMs;
        this.threadBlockedName = threadBlockedName;
        this.threadBlockedId = threadBlockedId;
        this.threadWaitedCount = threadWaitedCount;
        this.threadWaitedTimeMs = threadWaitedTimeMs;
        this.threadMaxCount = threadMaxCount;
        this.threadStack = threadStack;
        this.gmtModify = gmtModify;
    }

    public String getAppIp() {
        return appIp;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThreadStatus() {
        return threadStatus;
    }

    public String getThreadStartTimeMs() {
        return threadStartTimeMs;
    }

    public String getThreadBlockedCount() {
        return threadBlockedCount;
    }

    public String getThreadBlockedTimeMs() {
        return threadBlockedTimeMs;
    }

    public String getThreadBlockedName() {
        return threadBlockedName;
    }

    public String getThreadBlockedId() {
        return threadBlockedId;
    }

    public String getThreadWaitedCount() {
        return threadWaitedCount;
    }

    public String getThreadWaitedTimeMs() {
        return threadWaitedTimeMs;
    }

    public String getThreadMaxCount() {
        return threadMaxCount;
    }

    public String getThreadStack() {
        return threadStack;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    @Override
    public String toString() {
        return "QryThreadsInfoModel{" +
                "appIp='" + appIp + '\'' +
                ", threadId='" + threadId + '\'' +
                ", threadName='" + threadName + '\'' +
                ", threadStatus='" + threadStatus + '\'' +
                ", threadStartTimeMs='" + threadStartTimeMs + '\'' +
                ", threadBlockedCount='" + threadBlockedCount + '\'' +
                ", threadBlockedTimeMs='" + threadBlockedTimeMs + '\'' +
                ", threadBlockedName='" + threadBlockedName + '\'' +
                ", threadBlockedId='" + threadBlockedId + '\'' +
                ", threadWaitedCount='" + threadWaitedCount + '\'' +
                ", threadWaitedTimeMs='" + threadWaitedTimeMs + '\'' +
                ", threadMaxCount='" + threadMaxCount + '\'' +
                ", threadStack='" + threadStack + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }
}
