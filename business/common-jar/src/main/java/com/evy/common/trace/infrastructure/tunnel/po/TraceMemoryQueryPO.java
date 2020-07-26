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
    private String tamiAppIp;
    private int limit;
    private String tamiId;

    private TraceMemoryQueryPO(String tamiAppIp, int limit) {
        this.tamiAppIp = tamiAppIp;
        this.limit = limit;
    }

    private TraceMemoryQueryPO(String tamiId) {
        this.tamiId = tamiId;
    }

    public static TraceMemoryQueryPO create(String tamiId) {
        return new TraceMemoryQueryPO(tamiId);
    }

    public static TraceMemoryQueryPO create(String tamiAppIp, int limit) {
        return new TraceMemoryQueryPO(tamiAppIp, limit);
    }

    public static TraceMemoryQueryPO create(int limit) {
        return new TraceMemoryQueryPO(BusinessConstant.VM_HOST, limit);
    }
}
