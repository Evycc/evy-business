package com.evy.common.trace.infrastructure.tunnel.model;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/8 21:42
 */
@Getter
@ToString
public class TraceServiceModel {
    private final String beanName;
    private final String spcServiceName;
    private final String postPath;

    public TraceServiceModel(String beanName, String spcServiceName, String postPath) {
        this.beanName = beanName;
        this.spcServiceName = spcServiceName;
        this.postPath = postPath;
    }

    public static TraceServiceModel create(String beanName, String spcServiceName, String postPath) {
        return new TraceServiceModel(beanName, spcServiceName, postPath);
    }
}
