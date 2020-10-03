package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.BuildProjectDTO;
import com.evy.linlin.deploy.dto.BuildProjectOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import org.springframework.beans.BeanUtils;

/**
 * 异步编译应用
 * @Author: EvyLiuu
 * @Date: 2020/9/26 11:09
 */
public abstract class BuildProjectService extends BaseCommandTemplate<BuildProjectDTO, BuildProjectOutDTO> implements IBuildProject {
    @Override
    public BuildProjectOutDTO buildJar(BuildProjectDTO dto) {
        BuildProjectOutDTO buildProjectOutDTO = DeployAssembler.createBuildProjectOutDTO();
        BeanUtils.copyProperties(start(dto), buildProjectOutDTO);
        return buildProjectOutDTO;
    }
}
