package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.CreateDeployInfoDTO;
import com.evy.linlin.deploy.dto.CreateDeployInfoOutDTO;

/**
 * 创建一个新的部署配置
 * @Author: EvyLiuu
 * @Date: 2020/11/14 9:01
 */
public abstract class CreateDeployInfoService extends BaseCommandTemplate<CreateDeployInfoDTO, CreateDeployInfoOutDTO> implements ICreateDeployInfo {
    @Override
    public CreateDeployInfoOutDTO create(CreateDeployInfoDTO createDeployInfoDto) {

        return convertDto(null, start(createDeployInfoDto));
    }
}
