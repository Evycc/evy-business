package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryHttpInfoDTO;
import com.evy.linlin.trace.dto.QryHttpInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Http请求耗时及响应信息收集
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:42
 */
@RequestMapping
public interface IQryHttpInfo {
    @PostMapping("/qryHttpInfoList")
    QryHttpInfoOutDTO qryHttpInfoList(@RequestBody QryHttpInfoDTO qryHttpInfoDTO);
}
