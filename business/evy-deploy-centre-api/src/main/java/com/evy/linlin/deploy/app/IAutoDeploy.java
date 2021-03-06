package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 自动化部署
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:07
 */
@RequestMapping
public interface IAutoDeploy {
    /**
     * 异步部署指定应用
     */
    @PostMapping("/autoDeploy")
    AutoDeployOutDTO autoDeploy(@RequestBody AutoDeployDTO dto);
}
