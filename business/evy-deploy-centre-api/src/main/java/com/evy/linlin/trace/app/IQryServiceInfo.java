package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryServiceInfoDTO;
import com.evy.linlin.trace.dto.QryServiceInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Eureka服务发布及消费情况收集
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:42
 */
@RequestMapping
public interface IQryServiceInfo {
    @PostMapping("/qryServiceInfoList")
    QryServiceInfoOutDTO qryServiceInfoList(@RequestBody QryServiceInfoDTO qryServiceInfoDTO);
}
