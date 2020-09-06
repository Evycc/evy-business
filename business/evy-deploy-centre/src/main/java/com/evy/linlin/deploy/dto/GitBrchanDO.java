package com.evy.linlin.deploy.dto;

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

    public static GitBrchanDO convertFromDto(GetGitBrchansDTO dto) {
        return create(dto.getGitPath());
    }

    public static GitBrchanDO create(String gitPath) {
        return new GitBrchanDO(gitPath);
    }
}
