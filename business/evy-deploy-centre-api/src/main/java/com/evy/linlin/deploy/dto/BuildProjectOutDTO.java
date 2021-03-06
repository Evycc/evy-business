package com.evy.linlin.deploy.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 11:00
 */
public class BuildProjectOutDTO extends GatewayOutDTO {
    /**
     * 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    private String buildSeq;

    public BuildProjectOutDTO() {
    }

    public BuildProjectOutDTO(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    @Override
    public String toString() {
        return "BuildProjectOutDTO{" +
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
