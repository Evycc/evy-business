package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:56
 */
public class CreateDeployInfoOutDTO extends OutDTO {
    /**
     * 部署配置标识
     */
    private String deploySeq;

    public CreateDeployInfoOutDTO() {
    }

    public CreateDeployInfoOutDTO(String deploySeq) {
        this.deploySeq = deploySeq;
    }

    @Override
    public String toString() {
        return "CreateDeployInfoOutDTO{" +
                "deploySeq='" + deploySeq + '\'' +
                '}';
    }

    public String getDeploySeq() {
        return deploySeq;
    }

    public void setDeploySeq(String deploySeq) {
        this.deploySeq = deploySeq;
    }
}
