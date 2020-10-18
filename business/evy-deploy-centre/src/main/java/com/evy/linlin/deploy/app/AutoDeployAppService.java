package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.DeployShellRepository;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 部署应用到目标服务器
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:33
 */
@RestController
@TraceLog
public class AutoDeployAppService extends AutoDeployService {
    private final DeployShellRepository deployShellRepository;
    public AutoDeployAppService(DeployShellRepository deployShellRepository) {
        this.deployShellRepository = deployShellRepository;
    }

    @Override
    public AutoDeployOutDTO execute(AutoDeployDTO deployDTO) throws BasicException {
        deployShellRepository.autoDeploy(DeployAssembler.dtoConvertDo(deployDTO));
        return new AutoDeployOutDTO();
    }
}
