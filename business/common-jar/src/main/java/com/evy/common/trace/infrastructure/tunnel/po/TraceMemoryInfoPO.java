package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/5 19:00
 */
@Getter
@ToString
public class TraceMemoryInfoPO {
    private final String tamiAppIp;
    private final int tamiCpuCount;
    private final String tamiCpuLoad;
    private final String tamiSysMemory;
    private final String tamiSysAvailMemory;
    private final String tamiAppUseMemory;
    private final String tamiAppHeapMaxMemory;
    private final String tamiAppHeapMinMemory;
    private final String tamiAppHeapUseMemory;
    private final String tamiAppNonHeapMaxMemory;
    private final String tamiAppNonHeapMinMemory;
    private final String tamiAppNonHeapUseMemory;

    private TraceMemoryInfoPO(String tamiAppIp, int tamiCpuCount, String tamiCpuLoad, String tamiSysMemory, String tamiSysAvailMemory, String tamiAppUseMemory, String tamiAppHeapMaxMemory, String tamiAppHeapMinMemory, String tamiAppHeapUseMemory, String tamiAppNonHeapMaxMemory, String tamiAppNonHeapMinMemory, String tamiAppNonHeapUseMemory) {
        this.tamiAppIp = tamiAppIp;
        this.tamiCpuCount = tamiCpuCount;
        this.tamiCpuLoad = tamiCpuLoad;
        this.tamiSysMemory = tamiSysMemory;
        this.tamiSysAvailMemory = tamiSysAvailMemory;
        this.tamiAppUseMemory = tamiAppUseMemory;
        this.tamiAppHeapMaxMemory = tamiAppHeapMaxMemory;
        this.tamiAppHeapMinMemory = tamiAppHeapMinMemory;
        this.tamiAppHeapUseMemory = tamiAppHeapUseMemory;
        this.tamiAppNonHeapMaxMemory = tamiAppNonHeapMaxMemory;
        this.tamiAppNonHeapMinMemory = tamiAppNonHeapMinMemory;
        this.tamiAppNonHeapUseMemory = tamiAppNonHeapUseMemory;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceMemoryInfoPO create(String tamiCpuLoad, String tamiSysMemory, String tamiSysAvailMemory, String tamiAppUseMemory, String tamiAppHeapMaxMemory, String tamiAppHeapMinMemory, String tamiAppHeapUseMemory, String tamiAppNonHeapMaxMemory, String tamiAppNonHeapMinMemory, String tamiAppNonHeapUseMemory) {
        return new TraceMemoryInfoPO(BusinessConstant.VM_HOST, BusinessConstant.CORE_CPU_COUNT, tamiCpuLoad, tamiSysMemory, tamiSysAvailMemory, tamiAppUseMemory, tamiAppHeapMaxMemory, tamiAppHeapMinMemory, tamiAppHeapUseMemory, tamiAppNonHeapMaxMemory, tamiAppNonHeapMinMemory, tamiAppNonHeapUseMemory);
    }
}
