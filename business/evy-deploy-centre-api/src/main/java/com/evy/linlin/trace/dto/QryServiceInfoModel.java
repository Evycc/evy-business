package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:17
 */
@ToString
@AllArgsConstructor
@Getter
public class QryServiceInfoModel extends CommandModel {
    private String appIp;
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
}
