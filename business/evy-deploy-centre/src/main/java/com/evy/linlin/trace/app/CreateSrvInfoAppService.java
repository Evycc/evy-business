package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.CreateSrvInfoDTO;
import com.evy.linlin.trace.dto.CreateSrvInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 新增服务
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:53
 */
@RestController(ServiceCodeConstant.CREATE_SRV_IFNO)
@TraceLog
public class CreateSrvInfoAppService extends CreateSrvInfoService {
    private final QryTraceInfoRepository qryTraceInfoRepository;

    public CreateSrvInfoAppService(QryTraceInfoRepository qryTraceInfoRepository) {
        this.qryTraceInfoRepository = qryTraceInfoRepository;
    }

    @Override
    public CreateSrvInfoOutDTO execute(CreateSrvInfoDTO createSrvInfoDTO) throws BasicException {
        CreateSrvInfoOutDTO outDTO = null;
        if (qryTraceInfoRepository.createNewSrvInfo(QryTraceAssembler.dtoConvertDo(createSrvInfoDTO))) {
            outDTO = QryTraceAssembler.createSrvInfoOutDTO();
        }
        return outDTO;
    }
}
