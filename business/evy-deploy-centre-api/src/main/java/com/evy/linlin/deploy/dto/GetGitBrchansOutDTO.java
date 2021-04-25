package com.evy.linlin.deploy.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

import java.util.List;

/**
 * getGitBrchans OutDTO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:12
 */
public class GetGitBrchansOutDTO extends OutDTO {
    private String gitPath;
    private List<String> branchs;

    public GetGitBrchansOutDTO() {
    }

    public GetGitBrchansOutDTO(String gitPath, List<String> branchs) {
        this.gitPath = gitPath;
        this.branchs = branchs;
    }

    @Override
    public String toString() {
        return "GetGitBrchansOutDTO{" +
                "gitPath='" + gitPath + '\'' +
                ", branchs=" + branchs +
                '}';
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public List<String> getBranchs() {
        return branchs;
    }

    public void setBranchs(List<String> branchs) {
        this.branchs = branchs;
    }
}
