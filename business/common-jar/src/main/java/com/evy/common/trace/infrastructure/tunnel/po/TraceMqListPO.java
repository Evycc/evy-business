package com.evy.common.trace.infrastructure.tunnel.po;

import java.util.List;

/**
 * Trace MQ对象PO类
 * @Author: EvyLiuu
 * @Date: 2020/6/14 10:18
 */
public class TraceMqListPO {
    private List<TraceMqPO> mqPoList;

    private TraceMqListPO(List<TraceMqPO> mqPoList) {
        this.mqPoList = mqPoList;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceMqListPO create(List<TraceMqPO> mqPoList) {
        return new TraceMqListPO(mqPoList);
    }

    public List<TraceMqPO> getMqPoList() {
        return mqPoList;
    }
}
