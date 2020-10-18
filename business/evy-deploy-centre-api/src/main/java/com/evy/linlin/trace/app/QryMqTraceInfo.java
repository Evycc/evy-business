package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QryMqTraceInfoDTO;
import com.evy.linlin.trace.dto.QryMqTraceInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询、收集MQ消息消费链路、耗时情况
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:41
 */
@RequestMapping
public interface QryMqTraceInfo {
    @PostMapping("/qryMqTraceInfoList")
    QryMqTraceInfoOutDTO qryMqTraceInfoList(@RequestBody QryMqTraceInfoDTO qryMqTraceInfoDTO);
}
