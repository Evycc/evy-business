package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel;
import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2021/4/5 22:29
 */
public class QryDumpInfoOutDTO extends GatewayOutDTO {
    private List<ThreadDumpInfoModel> deadThreadList;
    private ThreadDumpInfoModel threadInfo;
    private HeapDumpInfoModel heapDumpInfo;

    public QryDumpInfoOutDTO() {
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
        return "QryDumpInfoOutDTO{" +
                "deadThreadList=" + deadThreadList +
                ", threadInfo=" + threadInfo +
                ", heapDumpInfo=" + heapDumpInfo +
                '}';
    }
}
