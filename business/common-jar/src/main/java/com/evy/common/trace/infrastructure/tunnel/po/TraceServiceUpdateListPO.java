package com.evy.common.trace.infrastructure.tunnel.po;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/11 17:14
 */
public class TraceServiceUpdateListPO {
    private final List<TraceServiceUpdatePO> poList;

    private TraceServiceUpdateListPO(List<TraceServiceUpdatePO> poList) {
        this.poList = poList;
    }

    public static TraceServiceUpdateListPO create(List<TraceServiceUpdatePO> poList) {
        return new TraceServiceUpdateListPO(poList);
    }

    public List<TraceServiceUpdatePO> getPoList() {
        return poList;
    }
}
