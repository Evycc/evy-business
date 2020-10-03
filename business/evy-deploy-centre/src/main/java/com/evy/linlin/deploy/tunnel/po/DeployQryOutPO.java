package com.evy.linlin.deploy.tunnel.po;

import com.evy.linlin.deploy.dto.DeployInfoDTO;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:42
 */
@Getter
@ToString
public class DeployQryOutPO {
    private String buildSeq;
    private String userSeq;
    private String projectName;
    private String appName;
    private String gitPath;
    private String gitBrchan;
    private String stageFlag;
    private Integer switchJunit;
    private Integer switchBatchDeploy;
    private String jarPath;
    private String jvmParam;
    private String targetHost;
    private String remarks;
    private String createDateTime;
}
