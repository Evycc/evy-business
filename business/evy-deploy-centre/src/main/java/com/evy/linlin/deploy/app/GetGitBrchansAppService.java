package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.DeployRepository;
import com.evy.linlin.deploy.dto.GetGitBrchansDTO;
import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import com.evy.linlin.deploy.tunnel.dto.GitBrchanDO;
import com.evy.linlin.deploy.tunnel.dto.GitBrchanOutDO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 获取git路径下分支集合
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:18
 */
@RestController
@TraceLog
public class GetGitBrchansAppService extends GetGitBrchansService {
    private final DeployRepository deployRepository;

    public GetGitBrchansAppService(DeployRepository deployRepository) {
        this.deployRepository = deployRepository;
    }

    @Override
    public GetGitBrchansOutDTO execute(GetGitBrchansDTO dto) throws BasicException {
        return GitBrchanOutDO.convertToDto(deployRepository.getGitBrchansShell(GitBrchanDO.convertFromDto(dto)));
    }
}
