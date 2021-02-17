package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.deploy.domain.repository.DeployShellRepository;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.dto.BuildProjectDTO;
import com.evy.linlin.deploy.dto.BuildProjectOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 异步编译应用
 * @Author: EvyLiuu
 * @Date: 2020/9/26 19:43
 */
@RestController(ServiceCodeConstant.BUILD_PROJECT_SERVICE_CODE)
@TraceLog
public class BuildProjectAppService extends BuildProjectService {
    private final DeployShellRepository deployShellRepository;

    public BuildProjectAppService(DeployShellRepository deployShellRepository) {
        this.deployShellRepository = deployShellRepository;
    }

    @Override
    public BuildProjectOutDTO execute(BuildProjectDTO dto) throws BasicException {
        deployShellRepository.build(DeployAssembler.dtoConvertDo(dto));
        return DeployAssembler.createBuildProjectOutDTO(ErrorConstant.SUCCESS, dto.getBuildSeq());
    }
}
