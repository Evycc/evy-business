package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryAppMermoryInfoDTO;
import com.evy.linlin.trace.dto.QryAppMermoryInfoModel;
import com.evy.linlin.trace.dto.QryAppMermoryInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: 查询服务器内存使用信息
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:11
 */
@RestController
@TraceLog
public class QryAppMermoryInfoAppService extends QryAppMermoryInfoService {
    private final QryTraceInfoRepository qryTraceInfoRepository;

    public QryAppMermoryInfoAppService(QryTraceInfoRepository qryTraceInfoRepository) {
        this.qryTraceInfoRepository = qryTraceInfoRepository;
    }

    @Override
    public QryAppMermoryInfoOutDTO execute(QryAppMermoryInfoDTO qryAppMermoryInfoDTO) throws BasicException {
        List<QryAppMermoryInfoModel> modelList = qryTraceInfoRepository.qryAppMermoryInfoList(QryTraceAssembler.dtoConvertDo(qryAppMermoryInfoDTO));
        return QryTraceAssembler.createQryAppMermoryInfoOutDTO(modelList);
    }
}
