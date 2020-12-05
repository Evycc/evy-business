package com.evy.common.command.infrastructure.tunnel.dto;

import com.evy.common.command.infrastructure.constant.ErrorConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 接口出参DTO
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
@ToString
public class OutDTO implements Serializable {
    private static final long serialVersionUID = 546422L;
    @Getter
    @Setter
    private String errorCode = ErrorConstant.SUCCESS;
    @Getter
    @Setter
    private String errorMsg = "交易成功";
}
