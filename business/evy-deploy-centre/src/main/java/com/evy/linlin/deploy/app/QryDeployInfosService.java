package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.QryDeployInfoDTO;
import com.evy.linlin.deploy.dto.QryDeployInfoOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import org.springframework.beans.BeanUtils;

/**
 * IQryDeployInfos 实现类
 * @Author: EvyLiuu
 * @Date: 2020/10/2 23:27
 */
public abstract class QryDeployInfosService extends BaseCommandTemplate<QryDeployInfoDTO, QryDeployInfoOutDTO> implements IQryDeployInfos {
    @Override
    public QryDeployInfoOutDTO qryDeployInfosByUser(QryDeployInfoDTO qryDeployInfoDTO) {
        QryDeployInfoOutDTO qryDeployInfoOutDTO = DeployAssembler.createQryDeployInfoOutDto();
        BeanUtils.copyProperties(start(qryDeployInfoDTO), qryDeployInfoOutDTO);
        return qryDeployInfoOutDTO;
    }
}
