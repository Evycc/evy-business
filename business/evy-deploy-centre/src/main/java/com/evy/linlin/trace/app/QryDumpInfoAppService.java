package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.domain.tunnel.model.SearchDumpOutDO;
import com.evy.linlin.trace.dto.QryDumpInfoDTO;
import com.evy.linlin.trace.dto.QryDumpInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: heap dump及线程实时查询功能
 * @Author: EvyLiuu
 * @Date: 2021/4/5 22:48
 */
@RestController(ServiceCodeConstant.QRY_DUMP_UTILS)
@TraceLog
public class QryDumpInfoAppService extends QryDumpInfoService {
    private final QryTraceInfoRepository qryTraceInfoRepository;

    public QryDumpInfoAppService(QryTraceInfoRepository qryTraceInfoRepository) {
        this.qryTraceInfoRepository = qryTraceInfoRepository;
    }

    @Override
    public QryDumpInfoOutDTO execute(QryDumpInfoDTO qryDumpInfoDTO) throws BasicException {
        SearchDumpOutDO outDo = qryTraceInfoRepository.searchDump(QryTraceAssembler.dtoConvertDo(qryDumpInfoDTO));
        return QryTraceAssembler.createQryDumpInfoOutDTO(outDo);
    }
}
