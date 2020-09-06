package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.GetGitBrchansDTO;
import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import org.springframework.beans.BeanUtils;

/**
 * IGetGitBrchans实现类
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:27
 */
public abstract class GetGitBrchansService extends BaseCommandTemplate<GetGitBrchansDTO, GetGitBrchansOutDTO> implements IGetGitBrchans {

    @Override
    public GetGitBrchansOutDTO getGitBrchans(GetGitBrchansDTO dto) {
        GetGitBrchansOutDTO getGitBrchansOutDto = new GetGitBrchansOutDTO();
        BeanUtils.copyProperties(start(dto), getGitBrchansOutDto);
        return getGitBrchansOutDto;
    }
}
