package com.evy.common.trace.infrastructure.tunnel.model;

import com.evy.common.utils.JsonUtils;

/**
 * 健康信息模型
 * @Author: EvyLiuu
 * @Date: 2021/4/29 1:10
 */
public class HealthyInfoModel {
    /**
     * 健康信息模型的类名
     */
    private String classCodeName;
    /**
     * 健康信息JSON串
     */
    private String traceInfoJson;
    /**
     * 应用名
     */
    private String appName = "default";

    private HealthyInfoModel() {
    }

    /**
     * 创建HealthyInfoModel
     * @param traceInfoModel 健康信息模型,如com.evy.common.trace.infrastructure.tunnel.model.TraceDBModel
     * @return com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel
     */
    public static HealthyInfoModel create(Object traceInfoModel) {
        HealthyInfoModel healthyInfoModel = new HealthyInfoModel();
        healthyInfoModel.setClassCodeName(traceInfoModel.getClass().getSimpleName());
        healthyInfoModel.setTraceInfoJson(JsonUtils.convertToJson(traceInfoModel));
        return healthyInfoModel;
    }

    /**
     * 创建HealthyInfoModel
     * @param traceInfoModel 健康信息模型,如com.evy.common.trace.infrastructure.tunnel.model.TraceDBModel
     * @param appName 应用名
     * @return com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel
     */
    public static HealthyInfoModel create(Object traceInfoModel, String appName) {
        HealthyInfoModel healthyInfoModel = HealthyInfoModel.create(traceInfoModel);
        healthyInfoModel.setAppName(appName);
        return healthyInfoModel;
    }

    /**
     * 创建HealthyInfoModel
     * @param clsCodeName 类名
     * @param traceInfoModel 健康信息模型,如com.evy.common.trace.infrastructure.tunnel.model.TraceDBModel
     * @return com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel
     */
    public static HealthyInfoModel create(Class<?> clsCodeName, Object traceInfoModel) {
        HealthyInfoModel healthyInfoModel = new HealthyInfoModel();
        healthyInfoModel.setClassCodeName(clsCodeName.getSimpleName());
        healthyInfoModel.setTraceInfoJson(JsonUtils.convertToJson(traceInfoModel));

        return healthyInfoModel;
    }

    /**
     * 创建HealthyInfoModel
     * @param clsCodeName 类名
     * @param traceInfoModel 健康信息模型,如com.evy.common.trace.infrastructure.tunnel.model.TraceDBModel
     * @param appName 应用名
     * @return com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel
     */
    public static HealthyInfoModel create(Class<?> clsCodeName, Object traceInfoModel, String appName) {
        HealthyInfoModel healthyInfoModel = HealthyInfoModel.create(clsCodeName, traceInfoModel);
        healthyInfoModel.setAppName(appName);
        return healthyInfoModel;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClassCodeName() {
        return classCodeName;
    }

    public void setClassCodeName(String classCodeName) {
        this.classCodeName = classCodeName;
    }

    public String getTraceInfoJson() {
        return traceInfoJson;
    }

    public void setTraceInfoJson(String traceInfoJson) {
        this.traceInfoJson = traceInfoJson;
    }
}
