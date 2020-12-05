package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.GetGitBrchansDTO;
import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 获取git对应分支集合
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:16
 */
@RequestMapping
public interface IGetGitBrchans {
    /**
     * 获取git对应分支集合
     * @param dto   com.evy.linlin.deploy.domain.repository.tunnel.dto.getGitBrchansDTO
     * @return      com.evy.linlin.deploy.domain.repository.tunnel.dto.getGitBrchansOutDTO
     */
    @PostMapping("/getGitBrchans")
    GetGitBrchansOutDTO getGitBrchans(@RequestBody GetGitBrchansDTO dto);
}
