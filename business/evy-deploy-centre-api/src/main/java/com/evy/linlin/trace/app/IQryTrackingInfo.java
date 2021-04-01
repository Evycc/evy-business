package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryTrackingInfoDTO;
import com.evy.linlin.trace.dto.QryTrackingInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询链路信息
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:25
 */
@RequestMapping
public interface IQryTrackingInfo {
    @PostMapping("/qryTraceList")
    QryTrackingInfoOutDTO qryTraceList(@RequestBody QryTrackingInfoDTO dto);
}
