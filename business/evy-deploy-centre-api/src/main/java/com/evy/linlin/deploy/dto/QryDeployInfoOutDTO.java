package com.evy.linlin.deploy.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * 返回用户名下所有部署应用信息,用于后续执行部署
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:07
 */
public class QryDeployInfoOutDTO extends GatewayOutDTO {
    List<DeployInfoDTO> dtoList;

    public QryDeployInfoOutDTO() {
    }

    public QryDeployInfoOutDTO(List<DeployInfoDTO> dtoList) {
        this.dtoList = dtoList;
    }

    @Override
    public String toString() {
        return "QryDeployInfoOutDTO{" +
                "dtoList=" + dtoList +
                '}';
    }

    public List<DeployInfoDTO> getDtoList() {
        return dtoList;
    }

    public void setDtoList(List<DeployInfoDTO> dtoList) {
        this.dtoList = dtoList;
    }
}
