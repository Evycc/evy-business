package com.evy.common.command.infrastructure.tunnel.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 接口出参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@ToString
public class OutDTO{
    @Getter
    @Setter
    private String errorCode = "0";
    @Getter
    @Setter
    private String errorMsg;
}
