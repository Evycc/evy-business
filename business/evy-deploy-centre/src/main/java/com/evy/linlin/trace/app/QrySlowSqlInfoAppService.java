package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.QrySlowSqlInfoDTO;
import com.evy.linlin.trace.dto.QrySlowSqlInfoModel;
import com.evy.linlin.trace.dto.QrySlowSqlInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外功能: 查询慢SQL、及优化建议
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:34
 */
@RestController(ServiceCodeConstant.QRY_SLOW_SQL_SERVICE_CODE)
@TraceLog
public class QrySlowSqlInfoAppService extends QrySlowSqlInfoService {
    private final QryTraceInfoRepository repository;

    public QrySlowSqlInfoAppService(QryTraceInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public QrySlowSqlInfoOutDTO execute(QrySlowSqlInfoDTO qrySlowSqlInfoDTO) throws BasicException {
        List<QrySlowSqlInfoModel> models = repository.qryAppSlowSqlList(QryTraceAssembler.dtoConvertDo(qrySlowSqlInfoDTO));
        return QryTraceAssembler.createQrySlowSqlInfoOutDTO(models);
    }
}
