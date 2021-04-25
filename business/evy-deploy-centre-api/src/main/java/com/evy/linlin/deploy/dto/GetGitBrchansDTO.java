package com.evy.linlin.deploy.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * getGitBrchans 入参DTO
 * @Author: EvyLiuu
 * @Date: 2020/9/5 23:10
 */
public class GetGitBrchansDTO extends InputDTO implements ValidatorDTO<GetGitBrchansDTO> {
    @NotBlank(message = "gitPath不能为空")
    @Length(max = 1024, message = "gitPath长度超限")
    private String gitPath;

    @Override
    public String toString() {
        return "GetGitBrchansDTO{" +
                "gitPath='" + gitPath + '\'' +
                '}';
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }
}
