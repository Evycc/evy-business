package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;

/**
 * 自动化部署
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:07
 */
public interface IAutoDeploy {
    AutoDeployOutDTO autoDeploy(AutoDeployDTO dto);
}
