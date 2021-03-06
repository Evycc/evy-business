package com.evy.linlin.login.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.login.dto.LoginServiceDTO;
import com.evy.linlin.login.dto.LoginServiceOutDTO;

/**
 * ILoginService实现类
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:07
 */
public abstract class LoginService extends BaseCommandTemplate<LoginServiceDTO, LoginServiceOutDTO> implements ILoginService {
    @Override
    public LoginServiceOutDTO login(LoginServiceDTO loginServiceDto) {
        return convertOutDto(start(loginServiceDto), new LoginServiceOutDTO());
    }
}
