package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryHttpInfoDTO;
import com.evy.linlin.trace.dto.QryHttpInfoModel;
import com.evy.linlin.trace.dto.QryHttpInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: Http请求耗时及响应信息查询
 * @Author: EvyLiuu
 * @Date: 2020/10/17 21:39
 */
@RestController
@TraceLog
public class QryHttpInfoAppService extends QryHttpInfoService {
    private final QryTraceInfoRepository repository;

    public QryHttpInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryHttpInfoOutDTO execute(QryHttpInfoDTO qryHttpInfoDTO) throws BasicException {
        List<QryHttpInfoModel> models = repository.qryHttpReqInfoList(QryTraceAssembler.dtoConvertDo(qryHttpInfoDTO));
        return QryTraceAssembler.createQryHttpInfoOutDTO(models);
    }
}
