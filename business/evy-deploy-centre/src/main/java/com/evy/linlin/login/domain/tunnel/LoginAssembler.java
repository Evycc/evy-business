package com.evy.linlin.login.domain.tunnel;

import com.evy.linlin.deploy.dto.LoginServiceDTO;
import com.evy.linlin.deploy.dto.LoginServiceOutDTO;
import com.evy.linlin.login.domain.tunnel.model.LoginInfoDO;
import com.evy.linlin.login.domain.tunnel.model.LoginInfoOutDO;

/**
 *  用于装配DTO、DO、PO之间的关系<br/>
 *  充当与repository之间的粘合剂
 * @Author: EvyLiuu
 * @Date: 2021/2/17 17:22
 */
public class LoginAssembler {
    /*---------------- dtoConvertDo DTO转DO ----------------*/

    public static LoginInfoDO dtoConvertDo(LoginServiceDTO dto) {
        LoginInfoDO loginInfoDO = new LoginInfoDO();
        loginInfoDO.setUserName(dto.getUsername());
        loginInfoDO.setPassword(dto.getPassword());
        return loginInfoDO;
    }

    /*---------------- doConvertDto DO转DTO ----------------*/

    public static LoginServiceOutDTO doConvertDto(LoginInfoOutDO loginInfoOutDo) {
        return new LoginServiceOutDTO(loginInfoOutDo.getUserSeq());
    }

    /*---------------- create 创建实例 ----------------*/

    public static LoginInfoOutDO create(String userSeq) {
        return new LoginInfoOutDO(userSeq);
    }
}
