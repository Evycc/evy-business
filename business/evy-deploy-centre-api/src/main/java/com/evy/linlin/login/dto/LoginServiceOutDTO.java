package com.evy.linlin.login.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2021/2/17 16:58
 */
@ToString
@Getter
@AllArgsConstructor
public class LoginServiceOutDTO extends OutDTO {
    /**
     * 唯一用户标识,以后通过该标识关联部署信息
     */
    private final String userSeq;
}
