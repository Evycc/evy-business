package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.CreateDeployInfoDTO;
import com.evy.linlin.deploy.dto.CreateDeployInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 创建一个新的部署配置
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:46
 */
@RequestMapping
public interface ICreateDeployInfo {
    /**
     * 创建用户名下新部署配置
     * @param createDeployInfoDto 返回部署配置唯一序列
     * @return com.evy.linlin.deploy.dto.CreateDeployInfoOutDTO
     */
    @PostMapping("/create")
    CreateDeployInfoOutDTO create(CreateDeployInfoDTO createDeployInfoDto);
}
