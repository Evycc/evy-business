package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryThreadsInfoDTO;
import com.evy.linlin.trace.dto.QryThreadsInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询线程信息,清晰展示线程阻塞、死锁情况
 * @Author: EvyLiuu
 * @Date: 2020/10/11 19:59
 */
@RequestMapping
public interface QryThreadsInfo {
    @PostMapping("/qryThreadsInfo")
    QryThreadsInfoOutDTO qryThreadsInfo(@RequestBody QryThreadsInfoDTO qryThreadsInfoDto);
}
