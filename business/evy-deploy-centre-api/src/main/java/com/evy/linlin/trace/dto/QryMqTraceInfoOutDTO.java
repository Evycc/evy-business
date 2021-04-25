package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
public class QryMqTraceInfoOutDTO extends OutDTO {
    private List<QryMqTraceInfoModel> list;

    public QryMqTraceInfoOutDTO() {
    }

    @Override
    public String toString() {
        return "QryMqTraceInfoOutDTO{" +
                "list=" + list +
                '}';
    }

    public List<QryMqTraceInfoModel> getList() {
        return list;
    }

    public void setList(List<QryMqTraceInfoModel> list) {
        this.list = list;
    }
}
