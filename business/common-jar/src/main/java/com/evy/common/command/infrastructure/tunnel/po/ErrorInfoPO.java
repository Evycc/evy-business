package com.evy.common.command.infrastructure.tunnel.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 错误码PO
 * @Author: EvyLiuu
 * @Date: 2020/9/6 1:14
 */
@Getter
@Setter
@ToString
public class ErrorInfoPO {
    private String errorCode;
    private String errorMsg;
}
