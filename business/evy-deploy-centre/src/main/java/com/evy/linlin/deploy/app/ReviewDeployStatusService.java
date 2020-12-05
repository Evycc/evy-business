package com.evy.linlin.deploy.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.deploy.domain.tunnel.DeployAssembler;
import com.evy.linlin.deploy.dto.ReviewStatusDTO;
import com.evy.linlin.deploy.dto.ReviewStatusOutDTO;
import org.springframework.beans.BeanUtils;

/**
 * IReviewDeployStatus 实现类
 * @Author: EvyLiuu
 * @Date: 2020/10/3 13:08
 */
public abstract class ReviewDeployStatusService extends BaseCommandTemplate<ReviewStatusDTO, ReviewStatusOutDTO> implements IReviewDeployStatus {
    @Override
    public ReviewStatusOutDTO reviewStatus(ReviewStatusDTO reviewStatusDTO) {
        ReviewStatusOutDTO reviewStatusOutDTO = DeployAssembler.createReviewStatusOutDTO();
        BeanUtils.copyProperties(start(reviewStatusDTO), reviewStatusOutDTO);
        return reviewStatusOutDTO;
    }
}
