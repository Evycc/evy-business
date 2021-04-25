package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
public class QryRedisInfoOutDTO extends OutDTO {
    private List<QryRedisInfoModel> list;

    public QryRedisInfoOutDTO() {
    }

    @Override
    public String toString() {
        return "QryRedisInfoOutDTO{" +
                "list=" + list +
                '}';
    }

    public List<QryRedisInfoModel> getList() {
        return list;
    }

    public void setList(List<QryRedisInfoModel> list) {
        this.list = list;
    }
}
