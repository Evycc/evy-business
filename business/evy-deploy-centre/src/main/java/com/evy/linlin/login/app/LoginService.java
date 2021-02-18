package com.evy.linlin.login.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.linlin.deploy.app.ILoginService;
import com.evy.linlin.deploy.dto.LoginServiceDTO;
import com.evy.linlin.deploy.dto.LoginServiceOutDTO;
import com.evy.linlin.login.domain.tunnel.LoginAssembler;
import org.springframework.beans.BeanUtils;

/**
 * ILoginService实现类
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:07
 */
public abstract class LoginService extends BaseCommandTemplate<LoginServiceDTO, LoginServiceOutDTO> implements ILoginService {
    @Override
    public LoginServiceOutDTO login(LoginServiceDTO loginServiceDto) {
        LoginServiceOutDTO outDto = new LoginServiceOutDTO(BusinessConstant.EMPTY_STR);
        BeanUtils.copyProperties(start(loginServiceDto), outDto);
        return outDto;
    }
}
