package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryTrackingInfoDTO;
import com.evy.linlin.trace.dto.QryTrackingInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 根据traceId查询链路调用信息
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:38
 */
@RestController(ServiceCodeConstant.QRY_TRACKING_LIST_INFO)
@TraceLog
public class QryTrackingInfoAppService extends QryTrackingInfoService {
    private final QryTraceInfoRepository repository;

    public QryTrackingInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryTrackingInfoOutDTO execute(QryTrackingInfoDTO dto) throws BasicException {
        return QryTraceAssembler.createQryTrackingInfoOutDTO(
                repository.qryTrackingInfo(QryTraceAssembler.dtoConvertDo(dto)));
    }
}
