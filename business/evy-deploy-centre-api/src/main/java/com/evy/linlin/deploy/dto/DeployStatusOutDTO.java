package com.evy.linlin.deploy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/13 16:11
 */
@Getter
@ToString
@AllArgsConstructor
public class DeployStatusOutDTO {
    private final String pid;
    private final String targetHost;
}
