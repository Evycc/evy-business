package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/11/14 8:56
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreateDeployInfoOutDTO extends OutDTO {
    /**
     * 部署配置标识
     */
    private String deploySeq;
}
