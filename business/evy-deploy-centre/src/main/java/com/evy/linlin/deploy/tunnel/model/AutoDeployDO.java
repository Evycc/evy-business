package com.evy.linlin.deploy.tunnel.model;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.linlin.deploy.dto.AutoDeployDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:09
 */
@Getter
@AllArgsConstructor
public class AutoDeployDO {
    /**
     * 编译成功唯一序列,用于关联数据库中对应jar包路径
     */
    private String buildSeq;
}
