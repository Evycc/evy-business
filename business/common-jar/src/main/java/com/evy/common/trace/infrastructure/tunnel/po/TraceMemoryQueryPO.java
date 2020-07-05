package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/5 19:38
 */
@Getter
@ToString
public class TraceMemoryQueryPO {
    private final String tamiAppIp;

    private TraceMemoryQueryPO(String tamiAppIp) {
        this.tamiAppIp = tamiAppIp;
    }

    public static TraceMemoryQueryPO create(String tamiAppIp) {
        return new TraceMemoryQueryPO(tamiAppIp);
    }

    public static TraceMemoryQueryPO create() {
        return new TraceMemoryQueryPO(BusinessConstant.VM_HOST);
    }
}
