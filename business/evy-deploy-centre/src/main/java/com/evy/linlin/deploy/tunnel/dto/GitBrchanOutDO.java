package com.evy.linlin.deploy.tunnel.dto;

import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * getGitBrchansShell 出参DO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:29
 */
@AllArgsConstructor
public class GitBrchanOutDO {
    private final String gitPath;
    private final List<String> gitBrchans;

    public static GetGitBrchansOutDTO convertToDto(GitBrchanOutDO gitBrchanOutDo) {
        return GetGitBrchansOutDTO.create(gitBrchanOutDo.gitPath, gitBrchanOutDo.gitBrchans);
    }

    public static GitBrchanOutDO create(String gitPath, List<String> brchans) {
        return new GitBrchanOutDO(gitPath, brchans);
    }
}
