package com.evy.linlin.deploy.app;

import com.evy.linlin.deploy.dto.ReviewStatusDTO;
import com.evy.linlin.deploy.dto.ReviewStatusOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 回查部署状态
 * @Author: EvyLiuu
 * @Date: 2020/10/3 12:53
 */
@RequestMapping
public interface IReviewDeployStatus {
    @PostMapping("/reviewStatus")
    ReviewStatusOutDTO reviewStatus(@RequestBody ReviewStatusDTO reviewStatusDTO);
}
