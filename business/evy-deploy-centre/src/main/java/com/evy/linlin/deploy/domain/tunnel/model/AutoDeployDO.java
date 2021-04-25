package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:09
 */
public class AutoDeployDO {
    /**
     * 编译成功唯一序列,用于关联数据库中对应jar包路径
     */
    private String buildSeq;

    public AutoDeployDO(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public String getBuildSeq() {
        return buildSeq;
    }
}
