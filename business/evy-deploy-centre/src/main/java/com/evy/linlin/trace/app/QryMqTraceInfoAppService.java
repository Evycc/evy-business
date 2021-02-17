package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryMqTraceInfoDTO;
import com.evy.linlin.trace.dto.QryMqTraceInfoModel;
import com.evy.linlin.trace.dto.QryMqTraceInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: 查询MQ消息消费链路、耗时情况
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:02
 */
@RestController(ServiceCodeConstant.QRY_MQ_INFO_SERVICE_CODE)
@TraceLog
public class QryMqTraceInfoAppService extends QryMqTraceInfoService {
    private final QryTraceInfoRepository repository;

    public QryMqTraceInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryMqTraceInfoOutDTO execute(QryMqTraceInfoDTO qryMqTraceInfoDTO) throws BasicException {
        List<QryMqTraceInfoModel> models = repository.qryMqTraceInfoList(QryTraceAssembler.dtoConvertDo(qryMqTraceInfoDTO));
        return QryTraceAssembler.createQryMqTraceInfoOutDTO(models);
    }
}
