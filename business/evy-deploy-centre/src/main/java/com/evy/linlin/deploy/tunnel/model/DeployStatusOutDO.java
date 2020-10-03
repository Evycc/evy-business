package com.evy.linlin.deploy.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 15:39
 */
@Getter
@ToString
@AllArgsConstructor
public class DeployStatusOutDO {
    private String pid;
    private String targetHost;
}
