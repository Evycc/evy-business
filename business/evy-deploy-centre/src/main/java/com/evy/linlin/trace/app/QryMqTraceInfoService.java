package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryMqTraceInfoDTO;
import com.evy.linlin.trace.dto.QryMqTraceInfoOutDTO;

/**
 * 查询MQ消息消费链路、耗时情况
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:01
 */
public abstract class QryMqTraceInfoService extends BaseCommandTemplate<QryMqTraceInfoDTO, QryMqTraceInfoOutDTO> implements QryMqTraceInfo {

    @Override
    public QryMqTraceInfoOutDTO qryMqTraceInfoList(QryMqTraceInfoDTO qryMqTraceInfoDTO) {
        return convertDto(new QryMqTraceInfoOutDTO(), start(qryMqTraceInfoDTO));
    }
}
