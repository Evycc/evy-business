package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;

/**
 * ICheckStart实现类
 * @Author: EvyLiuu
 * @Date: 2021/5/3 15:15
 */
public abstract class CheckStartService extends BaseCommandTemplate<AutoDeployDTO, AutoDeployOutDTO> implements ICheckStart {
    @Override
    public AutoDeployOutDTO checkStart(AutoDeployDTO dto) {
        return start(dto);
    }
}
