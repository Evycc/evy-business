package com.evy.linlin.deploy.app;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.linlin.deploy.domain.repository.DeployDataRepository;
import com.evy.linlin.deploy.dto.ReviewStatusDTO;
import com.evy.linlin.deploy.dto.ReviewStatusOutDTO;
import com.evy.linlin.deploy.tunnel.DeployAssembler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外功能: 回查部署状态
 * @Author: EvyLiuu
 * @Date: 2020/10/3 13:12
 */
@RestController
@TraceLog
public class ReviewDeployStatusAppService extends ReviewDeployStatusService {
    private final DeployDataRepository dataRepository;

    public ReviewDeployStatusAppService(DeployDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public ReviewStatusOutDTO execute(ReviewStatusDTO reviewStatusDTO) throws BasicException {
        return DeployAssembler.poConvertToDto(
                dataRepository.qryStageForSeq(
                        DeployAssembler.createFromBuildSeq(reviewStatusDTO.getBuildSeq())
                )
        );
    }
}