package com.evy.linlin.deploy.tunnel.dto;

import com.evy.linlin.deploy.dto.AutoDeployOutDTO;
import com.evy.linlin.deploy.dto.DeployStatusOutDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:09
 */
@Setter
@Getter
@NoArgsConstructor
public class AutoDeployOutDO {
    String errorCode;
    String msg;
    List<DeployStatusOutDO> deployStatusOutDOList;

    /**
     * 转换com.evy.linlin.deploy.tunnel.dto.AutoDeployOutDO => com.evy.linlin.deploy.dto.AutoDeployOutDTO
     * @param autoDeployOutDo com.evy.linlin.deploy.tunnel.dto.AutoDeployOutDO
     * @return com.evy.linlin.deploy.dto.AutoDeployOutDTO
     */
    public static AutoDeployOutDTO convertFromDto(AutoDeployOutDO autoDeployOutDo) {
        List<DeployStatusOutDTO> deployStatusOutDtoList = autoDeployOutDo.getDeployStatusOutDOList()
                .stream()
                .map(doList -> {
                    return DeployStatusOutDTO.create(doList.getPid(), doList.getTargetHost());
                })
                .collect(Collectors.toList());
        AutoDeployOutDTO autoDeployOutDto = new AutoDeployOutDTO(deployStatusOutDtoList);
        autoDeployOutDo.setErrorCode(autoDeployOutDo.getErrorCode());
        autoDeployOutDo.setMsg(autoDeployOutDo.getMsg());

        return autoDeployOutDto;
    }
}
