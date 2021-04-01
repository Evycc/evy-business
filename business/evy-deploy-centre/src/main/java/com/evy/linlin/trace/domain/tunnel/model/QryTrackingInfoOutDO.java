package com.evy.linlin.trace.domain.tunnel.model;

import com.evy.linlin.trace.dto.QryTrackingInfoModel;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/27 23:44
 */
public class QryTrackingInfoOutDO {
    private List<QryTrackingInfoModel> modelList;

    public QryTrackingInfoOutDO(List<QryTrackingInfoModel> modelList) {
        this.modelList = modelList;
    }

    public List<QryTrackingInfoModel> getModelList() {
        return modelList;
    }

    public void setModelList(List<QryTrackingInfoModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    public String toString() {
        return "QryTrackingInfoOutDO{" +
                "modelList=" + modelList +
                '}';
    }
}
