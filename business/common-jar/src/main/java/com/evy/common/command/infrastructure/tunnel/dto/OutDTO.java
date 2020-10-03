package com.evy.common.command.infrastructure.tunnel.dto;

import com.evy.common.command.infrastructure.constant.ErrorConstant;
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
    private String errorCode = ErrorConstant.SUCCESS;
    @Getter
    @Setter
    private String errorMsg = "交易成功";
}
