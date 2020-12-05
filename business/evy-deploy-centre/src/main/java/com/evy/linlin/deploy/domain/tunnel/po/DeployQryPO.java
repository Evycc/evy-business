package com.evy.linlin.deploy.domain.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:30
 */
@ToString
@Getter
@AllArgsConstructor
public class DeployQryPO {
    private String seq;
    private String userSeq;
    private String deploySeq;
}
