package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.CreateSrvInfoDTO;
import com.evy.linlin.trace.dto.CreateSrvInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 新增服务码
 *
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:39
 */
@RequestMapping
public interface ICreateSrvInfo {
    /**
     * 新增服务码
     */
    @PostMapping("/createSrvInfo")
    CreateSrvInfoOutDTO createSrvInfo(@RequestBody CreateSrvInfoDTO dto);
}
