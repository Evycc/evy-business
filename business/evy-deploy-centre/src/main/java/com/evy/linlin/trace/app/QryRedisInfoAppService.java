package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QryRedisInfoDTO;
import com.evy.linlin.trace.dto.QryRedisInfoModel;
import com.evy.linlin.trace.dto.QryRedisInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: 查询Redis服务器健康信息
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:28
 */
@RestController
@TraceLog
public class QryRedisInfoAppService extends QryRedisInfoService {
    private final QryTraceInfoRepository repository;

    public QryRedisInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QryRedisInfoOutDTO execute(QryRedisInfoDTO qryRedisInfoDTO) throws BasicException {
        List<QryRedisInfoModel> models = repository.qryRedisInfo(QryTraceAssembler.dtoConvertDo(qryRedisInfoDTO));
        return QryTraceAssembler.createQryRedisInfoOutDTO(models);
    }
}
