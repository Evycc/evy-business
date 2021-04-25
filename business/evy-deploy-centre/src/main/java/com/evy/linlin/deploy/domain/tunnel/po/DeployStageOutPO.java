package com.evy.linlin.deploy.domain.tunnel.po;

/**
 * 回查部署状态PO
 * @Author: EvyLiuu
 * @Date: 2020/10/3 13:18
 */
public class DeployStageOutPO {
    private String stageFlag;

    public DeployStageOutPO(String stageFlag) {
        this.stageFlag = stageFlag;
    }

    public String getStageFlag() {
        return stageFlag;
    }

    @Override
    public String toString() {
        return "DeployStageOutPO{" +
                "stageFlag='" + stageFlag + '\'' +
                '}';
    }
}
