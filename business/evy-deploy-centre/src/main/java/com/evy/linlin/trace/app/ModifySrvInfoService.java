package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.ModifySrvInfoDTO;
import com.evy.linlin.trace.dto.ModifySrvInfoOutDTO;

/**
 * 修改服务信息
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:55
 */
public abstract class ModifySrvInfoService extends BaseCommandTemplate<ModifySrvInfoDTO, ModifySrvInfoOutDTO> implements IModifySrvInfo {
    @Override
    public ModifySrvInfoOutDTO modifySrvInfo(ModifySrvInfoDTO dto) {
        return start(dto);
    }
}
