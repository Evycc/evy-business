package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/8 21:42
 */
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

    @Override
    public String toString() {
        return "TraceServiceModel{" +
                "beanName='" + beanName + '\'' +
                ", spcServiceName='" + spcServiceName + '\'' +
                ", postPath='" + postPath + '\'' +
                '}';
    }

    public String getBeanName() {
        return beanName;
    }

    public String getSpcServiceName() {
        return spcServiceName;
    }

    public String getPostPath() {
        return postPath;
    }
}
