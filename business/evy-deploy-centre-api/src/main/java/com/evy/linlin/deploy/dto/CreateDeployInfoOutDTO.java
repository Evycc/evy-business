package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.*;

/**
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:56
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateDeployInfoOutDTO extends OutDTO {
    /**
     * 部署配置标识
     */
    private String deploySeq;
}
