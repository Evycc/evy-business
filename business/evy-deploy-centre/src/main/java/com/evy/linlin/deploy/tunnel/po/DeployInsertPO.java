package com.evy.linlin.deploy.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:23
 */
@ToString
@Getter
@AllArgsConstructor
public class DeployInsertPO {
    private String seq;
    private String userSeq;
    private String projectName;
    private String appName;
    private String gitPath;
    private String gitBrchan;
    private String remarks;
    private String jvmParam;
    private String targetHost;
    private Integer switchBatchDeploy;
}
