package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryRedisInfoDTO;
import com.evy.linlin.trace.dto.QryRedisInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redis服务器健康信息收集
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:42
 */
@RequestMapping
public interface IQryRedisInfo {
    @PostMapping("/qryRedisInfoList")
    QryRedisInfoOutDTO qryRedisInfoList(@RequestBody QryRedisInfoDTO qryRedisInfoDTO);
}
