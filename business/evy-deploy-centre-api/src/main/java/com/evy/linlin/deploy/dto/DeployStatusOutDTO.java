package com.evy.linlin.deploy.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:11
 */
@Getter
@ToString
public class DeployStatusOutDTO {
    private final String pid;
    private final String targetHost;

    private DeployStatusOutDTO(String pid, String targetHost) {
        this.pid = pid;
        this.targetHost = targetHost;
    }

    public static DeployStatusOutDTO create(String pid, String targetHost) {
        return new DeployStatusOutDTO(pid, targetHost);
    }
}
