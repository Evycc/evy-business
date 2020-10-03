package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 11:00
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BuildProjectOutDTO extends OutDTO {
    /**
     * 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    private String buildSeq;
}
