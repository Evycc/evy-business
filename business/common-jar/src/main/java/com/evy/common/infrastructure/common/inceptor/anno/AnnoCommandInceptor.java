package com.evy.common.infrastructure.common.inceptor.anno;

import com.evy.common.infrastructure.common.inceptor.BaseCommandInceptor;

import java.lang.annotation.*;

/**
 * ConnamdExecute拦截器，基于Spring
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AnnoCommandInceptor {
    /**
     * 拦截器实现类
     * @return
     */
    Class<? extends BaseCommandInceptor>[] proxyClass();
}
