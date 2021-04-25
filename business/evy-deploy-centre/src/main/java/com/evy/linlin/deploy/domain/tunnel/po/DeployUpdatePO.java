package com.evy.linlin.deploy.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:26
 */
public class DeployUpdatePO {
    private String jarPath;
    private String stage;
    private String seq;
    private String buildLog;

    public DeployUpdatePO(String jarPath, String stage, String seq, String buildLog) {
        this.jarPath = jarPath;
        this.stage = stage;
        this.seq = seq;
        this.buildLog = buildLog;
    }

    public String getJarPath() {
        return jarPath;
    }

    public String getStage() {
        return stage;
    }

    public String getSeq() {
        return seq;
    }

    public String getBuildLog() {
        return buildLog;
    }

    @Override
    public String toString() {
        return "DeployUpdatePO{" +
                "jarPath='" + jarPath + '\'' +
                ", stage='" + stage + '\'' +
                ", seq='" + seq + '\'' +
                ", buildLog='" + buildLog + '\'' +
                '}';
    }
}
