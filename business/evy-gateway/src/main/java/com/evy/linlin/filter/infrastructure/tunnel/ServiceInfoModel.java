package com.evy.linlin.filter.infrastructure.tunnel;

import com.evy.linlin.filter.repository.po.ServiceInfoPO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/26 1:35
 */
public class ServiceInfoModel {
    private String serviceBeanName;
    private String serviceName;
    private String consumerHost;
    private String postPath;

    private ServiceInfoModel(String serviceBeanName, String serviceName, String consumerHost, String postPath) {
        this.serviceBeanName = serviceBeanName;
        this.serviceName = serviceName;
        this.consumerHost = consumerHost;
        this.postPath = postPath;
    }

    public static ServiceInfoModel convert(ServiceInfoPO serviceInfoPo) {
        return new ServiceInfoModel(serviceInfoPo.getTsiServiceBeanName(),
                serviceInfoPo.getTsiProvider(), serviceInfoPo.getTsiConsumerNames(), serviceInfoPo.getTsiServicePath());
    }

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getConsumerHost() {
        return consumerHost;
    }

    public String getPostPath() {
        return postPath;
    }
}
