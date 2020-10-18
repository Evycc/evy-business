package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.repository.DeployDataRepository;
import com.evy.linlin.deploy.dto.QryDeployInfoDTO;
import com.evy.linlin.deploy.dto.QryDeployInfoOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 查询用户部署记录
 *
 * @Author: EvyLiuu
 * @Date: 2020/10/2 23:33
 */
@RestController
@TraceLog
public class QryDeployInfosAppService extends QryDeployInfosService {
    private DeployDataRepository dataRepository;

    public QryDeployInfosAppService(DeployDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public QryDeployInfoOutDTO execute(QryDeployInfoDTO qryDeployInfoDTO) throws BasicException {
        return DeployAssembler.createQryDeployInfoOutDto(
                dataRepository.qryForUserSeq(
                        DeployAssembler.dtoConvertPo(qryDeployInfoDTO)
                )
        );
    }
}
