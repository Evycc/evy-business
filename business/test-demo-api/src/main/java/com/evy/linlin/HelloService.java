package com.evy.linlin;

import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 17:56
 */
@RequestMapping
public interface HelloService<T extends InputDTO, R extends OutDTO>{
    @PostMapping("/")
    R hello(@RequestBody T t);
}
