package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.ModifySrvInfoDTO;
import com.evy.linlin.trace.dto.ModifySrvInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 修改服务信息
 * @Author: EvyLiuu
 * @Date: 2021/3/13 17:43
 */
@RequestMapping
public interface IModifySrvInfo {
    /**
     * 修改服务信息
     */
    @PostMapping("/modifySrvInfo")
    ModifySrvInfoOutDTO modifySrvInfo(@RequestBody ModifySrvInfoDTO dto);
}
