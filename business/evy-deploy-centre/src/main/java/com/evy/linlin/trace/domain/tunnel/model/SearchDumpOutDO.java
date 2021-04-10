package com.evy.linlin.trace.domain.tunnel.model;

import com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2021/4/5 23:02
 */
public class SearchDumpOutDO {
    private List<ThreadDumpInfoModel> deadThreadList;
    private ThreadDumpInfoModel threadInfo;
    private HeapDumpInfoModel heapDumpInfo;

    public SearchDumpOutDO() {
    }

    public List<ThreadDumpInfoModel> getDeadThreadList() {
        return deadThreadList;
    }

    public void setDeadThreadList(List<ThreadDumpInfoModel> deadThreadList) {
        this.deadThreadList = deadThreadList;
    }

    public ThreadDumpInfoModel getThreadInfo() {
        return threadInfo;
    }

    public void setThreadInfo(ThreadDumpInfoModel threadInfo) {
        this.threadInfo = threadInfo;
    }

    public HeapDumpInfoModel getHeapDumpInfo() {
        return heapDumpInfo;
    }

    public void setHeapDumpInfo(HeapDumpInfoModel heapDumpInfo) {
        this.heapDumpInfo = heapDumpInfo;
    }

    @Override
    public String toString() {
        return "SearchDumpOutDO{" +
                "deadThreadList=" + deadThreadList +
                ", threadInfo=" + threadInfo +
                ", heapDumpInfo=" + heapDumpInfo +
                '}';
    }
}
