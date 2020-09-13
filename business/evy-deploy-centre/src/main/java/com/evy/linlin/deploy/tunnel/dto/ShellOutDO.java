package com.evy.linlin.deploy.tunnel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SHELL返回JSON结果
 * @Author: EvyLiuu
 * @Date: 2020/9/6 13:18
 */
@Setter
@Getter
@NoArgsConstructor
public class ShellOutDO {
    String errorCode;
    String msg;
}
