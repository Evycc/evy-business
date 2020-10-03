package com.evy.linlin.deploy.tunnel.constant;

/**
 * 部署阶段枚举值<br/>
 * jar部署阶段 0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败
 * @Author: EvyLiuu
 * @Date: 2020/9/26 12:41
 */
public enum DeployStageEnum {
    /**
     * 0b:编译中
     */
    BUILD_ING("0b", "编译中"),
    /**
     * 0a:编译成功
     */
    BUILD_SUCCESS("0a", "编译成功"),
    /**
     * 0c:编译失败
     */
    BUILD_FAILD("0c", "编译失败"),
    /**
     * 1a:部署成功
     */
    DEPLOY_ING("1b", "部署中"),
    /**
     * 1b:部署中
     */
    DEPLOY_SUCCESS("1a", "部署成功"),
    /**
     * 1c:部署失败
     */
    DEPLOY_FAILD("1c", "部署失败");

    String stageFlag;
    String stageMsg;
    DeployStageEnum(String stageFlag, String stageMsg) {
        this.stageFlag = stageFlag;
        this.stageMsg = stageMsg;
    }

    public String convertToMsg() {
        return stageMsg;
    }

    public String convertToFlag() {
        return stageFlag;
    }
}
