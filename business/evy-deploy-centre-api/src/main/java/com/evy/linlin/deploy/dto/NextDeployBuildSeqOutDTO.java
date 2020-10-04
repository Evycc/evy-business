package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:22
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NextDeployBuildSeqOutDTO extends OutDTO {
    /**
     * 返回编译唯一序列，用于关联一个编译记录
     */
    private String buildSeq;
}
