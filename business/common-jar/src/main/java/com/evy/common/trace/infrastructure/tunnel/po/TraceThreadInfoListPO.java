package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.trace.service.TraceThreadInfo;
import lombok.Getter;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/5 13:51
 */
@Getter
public class TraceThreadInfoListPO {
    private final List<TraceThreadInfoPO> traceThreadInfos;

    private TraceThreadInfoListPO(List<TraceThreadInfoPO> traceThreadInfos) {
        this.traceThreadInfos = traceThreadInfos;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceThreadInfoListPO create(List<TraceThreadInfoPO> traceThreadInfos) {
        return new TraceThreadInfoListPO(traceThreadInfos);
    }
}
