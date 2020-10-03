package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/3 12:56
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStatusOutDTO extends OutDTO {
    /**
     * 0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败
     */
    private String stageFlag;
}
