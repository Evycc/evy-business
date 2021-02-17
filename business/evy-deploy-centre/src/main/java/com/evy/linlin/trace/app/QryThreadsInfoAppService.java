package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryThreadsInfoDTO;
import com.evy.linlin.trace.dto.QryThreadsInfoModel;
import com.evy.linlin.trace.dto.QryThreadsInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: 查询线程信息,清晰展示线程阻塞、死锁情况
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:57
 */
@RestController(ServiceCodeConstant.QRY_THREAD_INFO_SERVICE_CODE)
@TraceLog
public class QryThreadsInfoAppService extends QryThreadsInfoService {
    private final QryTraceInfoRepository repository;

    public QryThreadsInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryThreadsInfoOutDTO execute(QryThreadsInfoDTO qryThreadsInfoDto) throws BasicException {
        List<QryThreadsInfoModel> models = repository.qryAppThreadInfoList(QryTraceAssembler.dtoConvertDo(qryThreadsInfoDto));
        return QryTraceAssembler.createQryThreadsInfoOutDTO(models);
    }
}
