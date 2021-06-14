package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.linlin.gateway.GatewayOutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/3 12:56
 */
public class ReviewStatusOutDTO extends GatewayOutDTO {
    /**
     * 0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败
     */
    private String stageFlag;

    public ReviewStatusOutDTO() {
    }

    public ReviewStatusOutDTO(String stageFlag) {
        this.stageFlag = stageFlag;
    }

    @Override
    public String toString() {
        return "ReviewStatusOutDTO{" +
                "stageFlag='" + stageFlag + '\'' +
                '}';
    }

    public String getStageFlag() {
        return stageFlag;
    }

    public void setStageFlag(String stageFlag) {
        this.stageFlag = stageFlag;
    }
}
