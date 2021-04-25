package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:17
 */
public class QryServiceInfoModel extends CommandModel {
    /**
     * 服务Bean名
     */
    private String serviceBeanName;
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务请求路径
     */
    private String servicePath;
    /**
     * 发布者应用名，单个
     */
    private String providerName;
    /**
     * 消费者应用名，多个
     */
    private List<String> consumerName;
    /**
     * 服务发布方，多个，格式 : 应用名@IP
     */
    private List<String> providerList;
    /**
     * 服务消费方，多个，格式 : 应用名@IP
     */
    private List<String> consumerList;
    /**
     * 服务更新时间
     */
    private String gmtModify;
    private Integer limitQps;
    private String limitFallback;

    public QryServiceInfoModel() {
    }

    @Override
    public String toString() {
        return "QryServiceInfoModel{" +
                "serviceBeanName='" + serviceBeanName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", servicePath='" + servicePath + '\'' +
                ", providerName='" + providerName + '\'' +
                ", consumerName=" + consumerName +
                ", providerList=" + providerList +
                ", consumerList=" + consumerList +
                ", gmtModify='" + gmtModify + '\'' +
                ", limitQps=" + limitQps +
                ", limitFallback='" + limitFallback + '\'' +
                '}';
    }

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    public void setServiceBeanName(String serviceBeanName) {
        this.serviceBeanName = serviceBeanName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public List<String> getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(List<String> consumerName) {
        this.consumerName = consumerName;
    }

    public List<String> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<String> providerList) {
        this.providerList = providerList;
    }

    public List<String> getConsumerList() {
        return consumerList;
    }

    public void setConsumerList(List<String> consumerList) {
        this.consumerList = consumerList;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(String gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getLimitQps() {
        return limitQps;
    }

    public void setLimitQps(Integer limitQps) {
        this.limitQps = limitQps;
    }

    public String getLimitFallback() {
        return limitFallback;
    }

    public void setLimitFallback(String limitFallback) {
        this.limitFallback = limitFallback;
    }
}
