package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.*;

import java.util.List;

/**
 * 返回用户名下所有部署应用信息,用于后续执行部署
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:07
 */
@Setter
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QryDeployInfoOutDTO extends OutDTO {
    List<DeployInfoDTO> dtoList;
}
