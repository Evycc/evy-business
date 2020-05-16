package com.evy.common.infrastructure.tunnel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 接口出参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@ToString
public class OutDTO{
    @Getter
    @Setter
    @NotNull
    private String errorCode = "0";
    @Getter
    @Setter
    private String errorMsg;
}
