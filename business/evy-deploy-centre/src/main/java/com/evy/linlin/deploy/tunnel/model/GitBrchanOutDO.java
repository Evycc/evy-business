package com.evy.linlin.deploy.tunnel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * getGitBrchansShell 出参DO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:29
 */
@AllArgsConstructor
@Getter
public class GitBrchanOutDO {
    private final String gitPath;
    private final List<String> gitBrchans;
}
