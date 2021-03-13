package com.evy.linlin.trace.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.domain.repository.QryTraceInfoRepository;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.dto.ModifySrvInfoDTO;
import com.evy.linlin.trace.dto.ModifySrvInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 修改服务信息
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:56
 */
@RestController(ServiceCodeConstant.MODIFY_SRV_IFNO)
@TraceLog
public class ModifySrvInfoAppService extends ModifySrvInfoService {
    private final QryTraceInfoRepository qryTraceInfoRepository;

    public ModifySrvInfoAppService(QryTraceInfoRepository qryTraceInfoRepository) {
        this.qryTraceInfoRepository = qryTraceInfoRepository;
    }

    @Override
    public ModifySrvInfoOutDTO execute(ModifySrvInfoDTO modifySrvInfoDTO) throws BasicException {
        ModifySrvInfoOutDTO outDTO = null;
        if (qryTraceInfoRepository.modifySrvInfo(QryTraceAssembler.dtoConvertDo(modifySrvInfoDTO))) {
            outDTO = QryTraceAssembler.createModifySrvInfoOutDTO();
        }
        return outDTO;
    }
}
