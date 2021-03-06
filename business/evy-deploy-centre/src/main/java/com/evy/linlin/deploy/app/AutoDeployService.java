package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;

/**
 * IAutoDeploy实现类
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:30
 */
public abstract class AutoDeployService extends BaseCommandTemplate<AutoDeployDTO, AutoDeployOutDTO> implements IAutoDeploy {
    @Override
    public AutoDeployOutDTO autoDeploy(AutoDeployDTO dto) {
        return convertOutDto(start(dto), new AutoDeployOutDTO());
    }
}
