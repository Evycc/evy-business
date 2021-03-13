package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.command.constant.ServiceCodeConstant;
import com.evy.linlin.trace.dto.CreateSrvInfoDTO;
import com.evy.linlin.trace.dto.CreateSrvInfoOutDTO;
import org.springframework.web.bind.annotation.RestController;

/**
 * 新增服务码
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:51
 */
@RestController(ServiceCodeConstant.MODIFY_SRV_IFNO)
@TraceLog
public abstract class CreateSrvInfoService extends BaseCommandTemplate<CreateSrvInfoDTO, CreateSrvInfoOutDTO> implements ICreateSrvInfo {
    @Override
    public CreateSrvInfoOutDTO createSrvInfo(CreateSrvInfoDTO dto) {
        return start(dto);
    }
}
