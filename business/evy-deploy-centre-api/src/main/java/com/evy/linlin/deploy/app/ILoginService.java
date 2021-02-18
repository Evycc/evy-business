package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.LoginServiceDTO;
import com.evy.linlin.deploy.dto.LoginServiceOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登录接口,做简单的登录操作即可,不存在的用户名密码直接创建登录成功
 * @Author: EvyLiuu
 * @Date: 2021/2/17 16:55
 */
@RequestMapping
public interface ILoginService {
    @PostMapping("/login")
    LoginServiceOutDTO login(@RequestBody LoginServiceDTO loginServiceDto);
}
