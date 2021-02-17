package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryServiceInfoDTO;
import com.evy.linlin.trace.dto.QryServiceInfoModel;
import com.evy.linlin.trace.dto.QryServiceInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 0:05
 */
@RestController(ServiceCodeConstant.QRY_SERVICE_INFO_SERVICE_CODE)
@TraceLog
public class QryServiceInfoAppService extends QryServiceInfoService {
    private final QryTraceInfoRepository repository;

    public QryServiceInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryServiceInfoOutDTO execute(QryServiceInfoDTO qryServiceInfoDTO) throws BasicException {
        List<QryServiceInfoModel> models = repository.qryAppServiceInfoList(QryTraceAssembler.dtoConvertDo(qryServiceInfoDTO));
        return QryTraceAssembler.createQryServiceInfoOutDTO(models);
    }
}
