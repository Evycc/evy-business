package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:18
 */
@Getter
public class QryAppMermoryListPO {
    private String tamiAppIp;
    private String tamiCpuCount;
    private String tamiCpuLoad;
    private String tamiSysMemory;
    private String tamiSysAvailMemory;
    private String tamiAppUseMemory;
    private String tamiAppHeapMaxMemory;
    private String tamiAppHeapMinMemory;
    private String tamiAppHeapUseMemory;
    private String tamiAppNoheapMaxMemory;
    private String tamiAppNoheapMinMemory;
    private String tamiAppNoheapUseMemory;
    private String gmtModify;
}
