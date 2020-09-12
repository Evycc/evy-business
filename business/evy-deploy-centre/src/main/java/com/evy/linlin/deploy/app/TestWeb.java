package com.evy.linlin.deploy.app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/12 15:19
 */
@RestController
public class TestWeb {
    @RequestMapping("/test")
    public String test(){
        return "HELLO";
    }
}
