package com.evy.linlin;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 17:56
 */
@RequestMapping
public interface HelloService<T extends InputDTO, R extends OutDTO>{
    @PostMapping("/hello")
    R hello(@RequestBody T t);
}
