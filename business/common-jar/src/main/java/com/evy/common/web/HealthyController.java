package com.evy.common.web;

import com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel;
import com.evy.common.trace.service.TraceJvmManagerUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 开放一个Controller,仅用于健康信息收集
 * @Author: EvyLiuu
 * @Date: 2021/4/5 17:27
 */
@RestController
@RequestMapping(value = HealthyControllerConstant.HEALTHY_CONTROLLER_CODE)
public class HealthyController {
    /**
     * 执行heap dump操作
     * @return com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel
     */
    @GetMapping(value = HealthyControllerConstant.HEAP_DUMP_CODE)
    public HeapDumpInfoModel heapDump() {
        return TraceJvmManagerUtils.heapDump();
    }

    /**
     * 查找指定线程,执行thread dump操作
     * @return com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel
     */
    @PostMapping(value = HealthyControllerConstant.THREAD_DUMP_CODE)
    public ThreadDumpInfoModel threadDump(@RequestBody long threadId) {
        return TraceJvmManagerUtils.threadDump(threadId);
    }

    /**
     * 检查是否存在死锁
     * @return com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel
     */
    @GetMapping(value = HealthyControllerConstant.DEAD_THREAD_DUMP_CODE)
    public List<ThreadDumpInfoModel> deadThreadDump() {
        return TraceJvmManagerUtils.findDeadThreads();
    }
}
