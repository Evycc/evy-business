package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.DeployShellRepository;
import com.evy.linlin.deploy.dto.BuildProjectDTO;
import com.evy.linlin.deploy.dto.BuildProjectOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import com.evy.linlin.deploy.tunnel.model.BuildInfoDO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 异步编译应用
 * @Author: EvyLiuu
 * @Date: 2020/9/26 19:43
 */
@RestController
@TraceLog
public class BuildProjectAppService extends BuildProjectService {
    private final DeployShellRepository deployShellRepository;

    public BuildProjectAppService(DeployShellRepository deployShellRepository) {
        this.deployShellRepository = deployShellRepository;
    }

    @Override
    public BuildProjectOutDTO execute(BuildProjectDTO dto) throws BasicException {
        deployShellRepository.build(DeployAssembler.doConvertToDto(dto));
        return DeployAssembler.createBuildProjectOutDTO(ErrorConstant.SUCCESS, dto.getBuildSeq());
    }
}
