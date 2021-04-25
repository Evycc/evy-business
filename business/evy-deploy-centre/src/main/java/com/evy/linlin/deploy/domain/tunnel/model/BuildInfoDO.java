package com.evy.linlin.deploy.domain.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 19:47
 */
public class BuildInfoDO {
    /**
     * 是否开启junit 0 开启 1 不开启
     */
    private boolean switchJunit;
    /**
     * 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    private String buildSeq;

    public BuildInfoDO(boolean switchJunit, String buildSeq) {
        this.switchJunit = switchJunit;
        this.buildSeq = buildSeq;
    }

    public boolean isSwitchJunit() {
        return switchJunit;
    }

    public String getBuildSeq() {
        return buildSeq;
    }
}
