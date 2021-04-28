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
     * 创建HealthyInfoModel JSON
     * @param traceInfoModel 健康信息模型,如com.evy.common.trace.infrastructure.tunnel.model.TraceDBModel
     * @return com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel
     */
    public static String createJson(Object traceInfoModel) {
        HealthyInfoModel healthyInfoModel = new HealthyInfoModel();
        healthyInfoModel.setClassCodeName(traceInfoModel.getClass().getName());
        healthyInfoModel.setTraceInfoJson(JsonUtils.convertToJson(traceInfoModel));
        return JsonUtils.convertToJson(healthyInfoModel);
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
