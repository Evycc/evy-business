package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.deploy.domain.repository.DeployDataRepository;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.domain.tunnel.po.DeployInsertPO;
import com.evy.linlin.deploy.dto.NextDeployBuildSeqDTO;
import com.evy.linlin.deploy.dto.NextDeployBuildSeqOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 生成唯一部署记录，并添加新一条部署信息记录
 * @Author: EvyLiuu
 * @Date: 2020/10/3 0:00
 */
@RestController(ServiceCodeConstant.NEXT_BUILD_SEQ_SERVICE_CODE)
@TraceLog
public class NextBuildSeqAppService extends NextBuildSeqService {
    private final DeployDataRepository dataRepository;

    public NextBuildSeqAppService(DeployDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public NextDeployBuildSeqOutDTO execute(NextDeployBuildSeqDTO dto) throws BasicException {
        DeployInsertPO deployInsertPo = DeployAssembler.dtoConvertPo(dto);
        dataRepository.insertDeployInfo(deployInsertPo);
        return DeployAssembler.createNextDeployBuildSeqOutDTO(deployInsertPo);
    }
}
