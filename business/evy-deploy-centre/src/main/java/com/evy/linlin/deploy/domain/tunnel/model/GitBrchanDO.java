package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * getGitBrchansShell 入参DO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 22:50
 */
public class GitBrchanDO {
    private final String gitPath;

    public GitBrchanDO(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getGitPath() {
        return gitPath;
    }
}
