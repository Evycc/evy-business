package com.evy.common.infrastructure.common.inceptor;

import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Command拦截器基类
 * <T extends InputDTO, R extends OutDTO>
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:11
 */
public interface CommandInceptor<T extends InputDTO> {
    /**
     * 前置拦截器
     * @param t 入参
     * @return  0 成功 1 失败
     * @throws BasicException   com.evy.common.infrastructure.common.exception.BasicException
     */
    int beforeCommand(T t) throws BasicException;

    /**
     * 后置拦截器
     * @param t 入参
     * @return  0 成功 1 失败
     * @throws BasicException   com.evy.common.infrastructure.common.exception.BasicException
     */
    int afterCommand(T t) throws BasicException;
}
