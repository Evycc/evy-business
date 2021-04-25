package com.evy.linlin.deploy.domain.tunnel.model;

import java.util.List;

/**
 * getGitBrchansShell 出参DO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:29
 */
public class GitBrchanOutDO {
    private final String gitPath;
    private final List<String> gitBrchans;

    public GitBrchanOutDO(String gitPath, List<String> gitBrchans) {
        this.gitPath = gitPath;
        this.gitBrchans = gitBrchans;
    }

    public String getGitPath() {
        return gitPath;
    }

    public List<String> getGitBrchans() {
        return gitBrchans;
    }
}
