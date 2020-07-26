package com.evy.common.trace.infrastructure.tunnel.po;

import lombok.Getter;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 17:14
 */
@Getter
public class TraceServiceUpdateListPO {
    private final List<TraceServiceUpdatePO> poList;

    private TraceServiceUpdateListPO(List<TraceServiceUpdatePO> poList) {
        this.poList = poList;
    }

    public static TraceServiceUpdateListPO create(List<TraceServiceUpdatePO> poList) {
        return new TraceServiceUpdateListPO(poList);
    }
}
