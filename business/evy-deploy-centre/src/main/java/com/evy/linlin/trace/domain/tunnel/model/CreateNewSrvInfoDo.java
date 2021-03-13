package com.evy.linlin.trace.domain.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 21:12
 */
@Getter
@AllArgsConstructor
public class CreateNewSrvInfoDo {
    private String srvCode;
    private String providerName;
    private String consumerName;
}
