package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 16:36
 */
@Getter
@ToString
public class QrySrvLimitInfoOutPO {
    private String srvCode;
    private String srvName;
    private Integer qpsLimit;
    private String fallback;
}
