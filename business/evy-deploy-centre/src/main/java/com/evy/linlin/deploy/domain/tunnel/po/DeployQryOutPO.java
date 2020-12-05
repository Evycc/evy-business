package com.evy.linlin.deploy.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:42
 */
@Getter
@ToString
public class DeployQryOutPO {
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
