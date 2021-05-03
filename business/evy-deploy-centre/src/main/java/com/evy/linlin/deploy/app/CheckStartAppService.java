package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.deploy.domain.repository.DeployShellRepository;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 异步检查服务器启动状态
 * @Author: EvyLiuu
 * @Date: 2021/5/3 15:16
 */
@RestController(ServiceCodeConstant.CHECK_START_CODE)
@TraceLog
public class CheckStartAppService extends CheckStartService{
    private final DeployShellRepository deployShellRepository;

    public CheckStartAppService(DeployShellRepository deployShellRepository) {
        this.deployShellRepository = deployShellRepository;
    }

    @Override
    public AutoDeployOutDTO execute(AutoDeployDTO autoDeployDTO) throws BasicException {
        deployShellRepository.checkStart(DeployAssembler.dtoConvertDo(autoDeployDTO));
        return new AutoDeployOutDTO();
    }
}
