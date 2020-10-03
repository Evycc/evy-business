package com.evy.linlin.deploy.tunnel.model;

import com.evy.linlin.deploy.dto.GetGitBrchansDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * getGitBrchansShell 入参DO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 22:50
 */
@AllArgsConstructor
@Getter
public class GitBrchanDO {
    private final String gitPath;
}
