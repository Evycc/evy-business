package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * 线程信息模型
 * @Author: EvyLiuu
 * @Date: 2021/4/4 21:38
 */
public class ThreadDumpInfoModel {
    /**
     * dump结果 0成功 1失败
     */
    private Integer dumpResult;
    /**
     * dump结果错误信息
     */
    private String dumpResultErrorText;
    /**
     * 线程堆栈信息
     */
    private String threadStack;
    /**
     * 线程状态
     */
    private String threadState;
    /**
     * 线程占用内存byte
     */
    private String threadAvailByte;
    /**
     * 线程获取锁的次数
     */
    private int threadBlockedCount;
    /**
     * 线程获取锁的持续时长,ms
     */
    private String threadBlockedTimeMs;
    /**
     * 线程锁名称
     */
    private String threadBlockedName;
    /**
     * 等待获取锁的线程ID,没在等待获取锁返回-1
     */
    private int threadBlockedId;
    /**
     * 因为该线程而处于等待状态的线程组<br/>
     * 格式: |分割
     */
    private String lockRelationThreadIds;
    /**
     * lockInfo
     */
    private String lockInfo;

    public ThreadDumpInfoModel() {
    }

    public String getThreadStack() {
        return threadStack;
    }

    public void setThreadStack(String threadStack) {
        this.threadStack = threadStack;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }

    public String getThreadAvailByte() {
        return threadAvailByte;
    }

    public void setThreadAvailByte(String threadAvailByte) {
        this.threadAvailByte = threadAvailByte;
    }

    public int getThreadBlockedCount() {
        return threadBlockedCount;
    }

    public void setThreadBlockedCount(int threadBlockedCount) {
        this.threadBlockedCount = threadBlockedCount;
    }

    public String getThreadBlockedTimeMs() {
        return threadBlockedTimeMs;
    }

    public void setThreadBlockedTimeMs(String threadBlockedTimeMs) {
        this.threadBlockedTimeMs = threadBlockedTimeMs;
    }

    public String getThreadBlockedName() {
        return threadBlockedName;
    }

    public void setThreadBlockedName(String threadBlockedName) {
        this.threadBlockedName = threadBlockedName;
    }

    public int getThreadBlockedId() {
        return threadBlockedId;
    }

    public void setThreadBlockedId(int threadBlockedId) {
        this.threadBlockedId = threadBlockedId;
    }

    public String getLockRelationThreadIds() {
        return lockRelationThreadIds;
    }

    public void setLockRelationThreadIds(String lockRelationThreadIds) {
        this.lockRelationThreadIds = lockRelationThreadIds;
    }

    public String getLockInfo() {
        return lockInfo;
    }

    public void setLockInfo(String lockInfo) {
        this.lockInfo = lockInfo;
    }

    public Integer getDumpResult() {
        return dumpResult;
    }

    public void setDumpResult(Integer dumpResult) {
        this.dumpResult = dumpResult;
    }

    public String getDumpResultErrorText() {
        return dumpResultErrorText;
    }

    public void setDumpResultErrorText(String dumpResultErrorText) {
        this.dumpResultErrorText = dumpResultErrorText;
    }
}
