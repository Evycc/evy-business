package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.NextDeployBuildSeqDTO;
import com.evy.linlin.deploy.dto.NextDeployBuildSeqOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 获取一个新的buildSeq,用于新部署任务
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:20
 */
@RequestMapping
public interface INextBuildSeq {
    /**
     * 获取一个新的buildSeq,用于新部署任务
     */
    @PostMapping("/nextBuildSeq")
    NextDeployBuildSeqOutDTO nextBuildSeq(@RequestBody NextDeployBuildSeqDTO dto);
}
