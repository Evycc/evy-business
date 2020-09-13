package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.DeployRepository;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import com.evy.linlin.deploy.tunnel.dto.AutoDeployDO;
import com.evy.linlin.deploy.tunnel.dto.AutoDeployOutDO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 部署应用到目标服务器
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:33
 */
@RestController
@TraceLog
public class AutoDeployAppService extends AutoDeployService {
    private final DeployRepository deployRepository;
    public AutoDeployAppService(DeployRepository deployRepository) {
        this.deployRepository = deployRepository;
    }

    @Override
    public AutoDeployOutDTO execute(AutoDeployDTO deployDTO) throws BasicException {
        return AutoDeployOutDO.convertFromDto(deployRepository.autoDeploy(AutoDeployDO.convertFromDto(deployDTO)));
    }
}
