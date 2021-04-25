package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:22
 */
public class NextDeployBuildSeqOutDTO extends OutDTO {
    /**
     * 返回编译唯一序列，用于关联一个编译记录
     */
    private String buildSeq;

    public NextDeployBuildSeqOutDTO() {
    }

    public NextDeployBuildSeqOutDTO(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    @Override
    public String toString() {
        return "NextDeployBuildSeqOutDTO{" +
                "buildSeq='" + buildSeq + '\'' +
                '}';
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }
}
