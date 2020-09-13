package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import org.springframework.beans.BeanUtils;

/**
 * IAutoDeploy实现类
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:30
 */
public abstract class AutoDeployService extends BaseCommandTemplate<AutoDeployDTO, AutoDeployOutDTO> implements IAutoDeploy {
    @Override
    public AutoDeployOutDTO autoDeploy(AutoDeployDTO dto) {
        AutoDeployOutDTO autoDeployOutDTO = new AutoDeployOutDTO();
        BeanUtils.copyProperties(start(dto), autoDeployOutDTO);
        return autoDeployOutDTO;
    }
}
