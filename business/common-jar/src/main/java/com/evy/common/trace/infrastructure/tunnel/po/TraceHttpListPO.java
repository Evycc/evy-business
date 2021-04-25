package com.evy.common.trace.infrastructure.tunnel.po;

import java.util.List;

/**
 * Trace Http对象PO类
 * @Author: EvyLiuu
 * @Date: 2020/6/14 17:51
 */
public class TraceHttpListPO {
    private final List<TraceHttpPO> httpsPos;

    private TraceHttpListPO(List<TraceHttpPO> httpsPos) {
        this.httpsPos = httpsPos;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceHttpListPO create(List<TraceHttpPO> httpsPos) {
        return new TraceHttpListPO(httpsPos);
    }

    public List<TraceHttpPO> getHttpsPos() {
        return httpsPos;
    }
}
