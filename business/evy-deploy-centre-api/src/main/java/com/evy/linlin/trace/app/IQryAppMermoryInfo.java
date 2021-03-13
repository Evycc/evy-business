package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryAppMermoryInfoDTO;
import com.evy.linlin.trace.dto.QryAppMermoryInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询应用内存使用率
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:40
 */
@RequestMapping
public interface IQryAppMermoryInfo {
    /**
     * 查询多个应用内存使用率
     */
    @PostMapping("/qryAppMemoryList")
    QryAppMermoryInfoOutDTO qryAppMemoryList(@RequestBody QryAppMermoryInfoDTO qryAppMermoryInfoDTO);
}
