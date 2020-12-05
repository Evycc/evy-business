package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.linlin.deploy.domain.repository.DeployDataRepository;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.domain.tunnel.po.DeployInsertPO;
import com.evy.linlin.deploy.dto.CreateDeployInfoDTO;
import com.evy.linlin.deploy.dto.CreateDeployInfoOutDTO;

/**
 * 对外功能: 创建一个新的部署配置
 * @Author: EvyLiuu
 * @Date: 2020/11/14 9:03
 */
public class CreateDeployInfoAppService extends CreateDeployInfoService {
    private final DeployDataRepository dataRepository;

    public CreateDeployInfoAppService(DeployDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public CreateDeployInfoOutDTO execute(CreateDeployInfoDTO createDeployInfoDTO) throws BasicException {
        DeployInsertPO deployInsertPo = DeployAssembler.dtoConvertPo(createDeployInfoDTO);
        dataRepository.insertDeployInfo(deployInsertPo);
        return DeployAssembler.createCreateDeployInfoOutDTO(deployInsertPo);
    }
}
