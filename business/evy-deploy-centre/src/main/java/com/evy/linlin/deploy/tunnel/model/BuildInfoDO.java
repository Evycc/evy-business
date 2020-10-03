package com.evy.linlin.deploy.tunnel.model;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.linlin.deploy.dto.BuildProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 19:47
 */
@Getter
@AllArgsConstructor
public class BuildInfoDO {
    /**
     * 是否开启junit 0 开启 1 不开启
     */
    private boolean switchJunit;
    /**
     * 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    private String buildSeq;
}
