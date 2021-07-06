package com.evy.linlin.trace.dto;

import com.evy.linlin.gateway.GatewayOutDTO;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:02
 */
public class QryThreadsInfoOutDTO extends GatewayOutDTO {
    private List<QryThreadsInfoModel> list;
    private Integer total;

    public QryThreadsInfoOutDTO() {
    }

    public List<QryThreadsInfoModel> getList() {
        return list;
    }

    public void setList(List<QryThreadsInfoModel> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
