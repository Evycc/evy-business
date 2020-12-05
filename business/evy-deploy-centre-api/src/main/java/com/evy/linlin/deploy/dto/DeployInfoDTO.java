package com.evy.linlin.deploy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 部署应用信息
 * @Author: EvyLiuu
 * @Date: 2020/9/26 23:09
 */
@Getter
@ToString
@AllArgsConstructor
public class DeployInfoDTO {
    private String buildSeq;
    private String deploySeq;
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
