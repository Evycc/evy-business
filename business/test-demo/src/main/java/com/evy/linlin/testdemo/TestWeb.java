package com.evy.linlin.testdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: EvyLiuu
 * @Date: 2019/12/15 22:03
 */
@RestController
public class TestWeb {
    @RequestMapping("/test")
    public String test(){
        return "HELLO";
    }
}
