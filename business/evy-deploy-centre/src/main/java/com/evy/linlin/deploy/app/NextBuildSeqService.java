package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.dto.NextDeployBuildSeqDTO;
import com.evy.linlin.deploy.dto.NextDeployBuildSeqOutDTO;

/**
 * INextBuildSeq 实现类
 * @Author: EvyLiuu
 * @Date: 2020/10/2 23:57
 */
public abstract class NextBuildSeqService extends BaseCommandTemplate<NextDeployBuildSeqDTO, NextDeployBuildSeqOutDTO> implements INextBuildSeq {
    @Override
    public NextDeployBuildSeqOutDTO nextBuildSeq(NextDeployBuildSeqDTO dto) {
        return convertOutDto(start(dto), new NextDeployBuildSeqOutDTO());
    }
}
