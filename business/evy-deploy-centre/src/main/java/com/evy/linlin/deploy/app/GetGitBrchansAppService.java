package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.deploy.domain.repository.DeployShellRepository;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.domain.tunnel.model.GitBrchanOutDO;
import com.evy.linlin.deploy.dto.GetGitBrchansDTO;
import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 获取git路径下分支集合
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:18
 */
@RestController(ServiceCodeConstant.GET_BRANCH_SERVICE_CODE)
@TraceLog
public class GetGitBrchansAppService extends GetGitBrchansService {
    private final DeployShellRepository deployShellRepository;

    public GetGitBrchansAppService(DeployShellRepository deployShellRepository) {
        this.deployShellRepository = deployShellRepository;
    }

    @Override
    public GetGitBrchansOutDTO execute(GetGitBrchansDTO dto) throws BasicException {
        GitBrchanOutDO gitBrchanOutDo = deployShellRepository.getGitBrchansShell(DeployAssembler.dtoConvertDo(dto));
        if (CollectionUtils.isEmpty(gitBrchanOutDo.getGitBrchans())) {
            throw new BasicException(ErrorConstant.ERROR_01, "获取分支为空");
        }

        return DeployAssembler.doConvertDto(gitBrchanOutDo);
    }
}
