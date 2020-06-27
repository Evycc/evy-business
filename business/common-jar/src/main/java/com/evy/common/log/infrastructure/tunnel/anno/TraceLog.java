package com.evy.common.log.infrastructure.tunnel.anno;

import java.lang.annotation.*;

/**
 * 交易结束时，记录到公共流水表
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TraceLog {
    /**
     * 从上下文中取字段及对应值
     * srcSendNo|clientIp|errorCode|errorMsg
     */
    String reqContent() default "";
}
