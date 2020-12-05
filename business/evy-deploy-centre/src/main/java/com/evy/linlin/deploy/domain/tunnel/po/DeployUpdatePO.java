package com.evy.linlin.deploy.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:26
 */
@ToString
@Getter
@AllArgsConstructor
public class DeployUpdatePO {
    private String jarPath;
    private String stage;
    private String seq;
    private String buildLog;
}
