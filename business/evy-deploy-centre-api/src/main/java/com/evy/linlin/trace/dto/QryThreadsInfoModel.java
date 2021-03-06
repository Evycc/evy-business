package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:01
 */
@ToString
@AllArgsConstructor
@Getter
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
}
