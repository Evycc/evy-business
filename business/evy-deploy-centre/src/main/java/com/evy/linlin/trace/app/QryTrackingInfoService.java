package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryTrackingInfoDTO;
import com.evy.linlin.trace.dto.QryTrackingInfoOutDTO;

/**
 * 根据traceId查询链路调用信息
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:36
 */
public abstract class QryTrackingInfoService extends BaseCommandTemplate<QryTrackingInfoDTO, QryTrackingInfoOutDTO> implements IQryTrackingInfo {
    @Override
    public QryTrackingInfoOutDTO qryTraceList(QryTrackingInfoDTO dto) {
        return start(dto);
    }
}
