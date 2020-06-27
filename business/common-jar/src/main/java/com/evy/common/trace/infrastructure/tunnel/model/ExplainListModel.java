package com.evy.common.trace.infrastructure.tunnel.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * explain 模型
 * @Author: EvyLiuu
 * @Date: 2020/6/20 14:53
 */
@Getter
@ToString
public class ExplainListModel {
    private final List<ExplainModel> explainModelList;

    private ExplainListModel(List<ExplainModel> explainModelList) {
        this.explainModelList = explainModelList;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static ExplainListModel create(List<ExplainModel> explainModelList) {
        return new ExplainListModel(explainModelList);
    }
}
