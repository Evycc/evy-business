package com.evy.common.infrastructure.common.command;

import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;

/**
 * Command基类模板
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public interface CommandTemplate<T extends InputDTO, R extends OutDTO> {
    /**
     * 执行前置拦截器操作
     * @param t 入参
     * @return  拦截器返回   0 成功 1 失败
     * @throws BasicException   com.evy.common.infrastructure.common.exception.BasicException
     */
    R before(T t) throws BasicException;

    /**
     * 执行关键方法，由子类实现
     * @param t 入参dto
     * @return  出参dto
     * @throws BasicException   com.evy.common.infrastructure.common.exception.BasicException
     */
    R execute(T t) throws BasicException;

    /**
     * 执行后置拦截器操作
     * @param t 入参
     * @return  拦截器返回   0 成功 1 失败
     * @throws BasicException   com.evy.common.infrastructure.common.exception.BasicException
     */
    R after(T t) throws BasicException;

    /**
     * 模板类执行入口
     * @param t 入参dto
     * @return  出参dto
     */
    R start(T t);
}
