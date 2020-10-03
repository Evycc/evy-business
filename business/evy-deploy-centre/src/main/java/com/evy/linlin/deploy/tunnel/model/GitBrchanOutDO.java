package com.evy.linlin.deploy.tunnel.model;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
import com.evy.linlin.deploy.dto.GetGitBrchansOutDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

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
