package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.BuildProjectDTO;
import com.evy.linlin.deploy.dto.BuildProjectOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 编译对应项目,Jar包的形式
 * @Author: EvyLiuu
 * @Date: 2020/9/26 11:03
 */
@RequestMapping
public interface IBuildProject {
    /**
     * 异步编译指定应用
     */
    @PostMapping("/buildJar")
    BuildProjectOutDTO buildJar(@RequestBody BuildProjectDTO dto);
}
