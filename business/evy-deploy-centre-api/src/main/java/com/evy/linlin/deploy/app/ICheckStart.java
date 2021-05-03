package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.AutoDeployDTO;
import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 异步检查服务器启动状态
 * @Author: EvyLiuu
 * @Date: 2021/5/3 15:14
 */
@RequestMapping
public interface ICheckStart {
    /**
     * 异步检查服务器启动状态
     */
    @PostMapping("/checkStart")
    AutoDeployOutDTO checkStart(@RequestBody AutoDeployDTO dto);
}
