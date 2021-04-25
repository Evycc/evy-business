package com.evy.common.command.app.inceptor;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;

/**
 * Command拓展类
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
public abstract class BaseCommandInceptor<T extends InputDTO> implements CommandInceptor<T> {
    /**
     * 拦截器执行顺序
     */
    private int order = 1;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
