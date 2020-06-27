package com.evy.common.command.app.inceptor;

import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;

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
     * @throws BasicException   com.evy.common.command.infrastructure.exception.BasicException
     */
    int beforeCommand(T t) throws BasicException;

    /**
     * 后置拦截器
     * @param t 入参
     * @return  0 成功 1 失败
     * @throws BasicException   com.evy.common.command.infrastructure.exception.BasicException
     */
    int afterCommand(T t) throws BasicException;
}
