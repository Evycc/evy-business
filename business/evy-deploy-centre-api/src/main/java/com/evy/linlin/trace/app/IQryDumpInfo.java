package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryDumpInfoDTO;
import com.evy.linlin.trace.dto.QryDumpInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * heap dump及线程实时查询功能
 * @Author: EvyLiuu
 * @Date: 2021/4/5 22:26
 */
@RequestMapping
public interface IQryDumpInfo {
    /**
     * 实时查看指定线程信息
     */
    @PostMapping("/threadDump")
    QryDumpInfoOutDTO threadDump(@RequestBody QryDumpInfoDTO dto);
    /**
     * 在线进行heap dump
     */
    @PostMapping("/heapDump")
    QryDumpInfoOutDTO findDeadThreads(@RequestBody QryDumpInfoDTO dto);
    /**
     * 查找是否存在死锁
     */
    @PostMapping("/findDeadThreads")
    QryDumpInfoOutDTO heapDump(@RequestBody QryDumpInfoDTO dto);
}
