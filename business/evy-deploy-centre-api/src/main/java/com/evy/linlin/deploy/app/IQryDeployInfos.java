package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.QryDeployInfoDTO;
import com.evy.linlin.deploy.dto.QryDeployInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询用户名下所有部署应用信息,用于后续执行部署
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:17
 */
@RequestMapping
public interface IQryDeployInfos {
    /**
     * 查询用户名下所有部署应用信息,用于后续执行部署
     */
    @PostMapping("/qryDeployInfosByUser")
    QryDeployInfoOutDTO qryDeployInfosByUser(@RequestBody QryDeployInfoDTO qryDeployInfoDTO);
}
