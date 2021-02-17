package com.evy.linlin.login.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.deploy.dto.LoginServiceDTO;
import com.evy.linlin.deploy.dto.LoginServiceOutDTO;
import com.evy.linlin.login.domain.repository.LoginDataRepository;
import com.evy.linlin.login.domain.tunnel.LoginAssembler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 登录部署中心,若用户信息不存在，则创建并返回userSeq唯一用户标识,反之进行校验
 *
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:10
 */
@RestController(ServiceCodeConstant.LOGIN_SERVICE_CODE)
@TraceLog
public class LoginAppService extends LoginService {
    private final LoginDataRepository repository;

    public LoginAppService(LoginDataRepository repository) {
        this.repository = repository;
    }

    /**
     * 前端通过md5加密后上送密码,需要解密后将明文重新加密后保存到redis
     *
     * @param loginServiceDTO 用户信息
     * @return 唯一用户标识
     * @throws BasicException 校验异常
     */
    @Override
    public LoginServiceOutDTO execute(LoginServiceDTO loginServiceDTO) throws BasicException {
        return LoginAssembler.doConvertDto(repository.validLogin(LoginAssembler.dtoConvertDo(loginServiceDTO)));
    }
}
