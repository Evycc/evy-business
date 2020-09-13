package com.evy.linlin.deploy.tunnel.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 15:39
 */
@Getter
@ToString
public class DeployStatusOutDO {
    private String pid;
    private String targetHost;

    private DeployStatusOutDO(String pid, String targetHost) {
        this.pid = pid;
        this.targetHost = targetHost;
    }

    public static DeployStatusOutDO create(String pid, String targetHost) {
        return new DeployStatusOutDO(pid, targetHost);
    }
}
