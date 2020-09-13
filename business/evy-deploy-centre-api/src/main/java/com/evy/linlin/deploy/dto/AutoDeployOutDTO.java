package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * AutoDeployOutDTO
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:05
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AutoDeployOutDTO extends OutDTO {
    List<DeployStatusOutDTO> deployStatusOutDtoList;

}
