package com.evy.linlin.common;

/**
 * 网关常量
 * @Author: EvyLiuu
 * @Date: 2020/7/25 17:30
 */
public class GwErrorCodeConstant {
    /**
     * 错误码：未找到对应服务
     */
    public static final String ERROR_SERVICE_NO_FOUND = "ERROR_SERVICE_NO_FOUND";
    /**
     * 错误信息：未找到对应服务
     */
    public static final String ERROR_MSG_SERVICE_NO_FOUND = "service no found";
    /**
     * 错误码：未找到对应服务
     */
    public static final String ERROR_SERVICE_GW_ERR = "ERROR_GATEWAY_ERR";
    /**
     * 错误信息：未找到对应服务
     */
    public static final String ERROR_MSG_GW_ERR = "gateway error";
    /**
     * 错误码：服务鉴权失败
     */
    public static final String ERROR_SERVICE_NO_AUTH = "ERROR_GATEWAY_NO_AUTH";
    /**
     * 错误信息：服务鉴权失败
     */
    public static final String ERROR_MSG_SERVICE_NO_AUTH = "gateway no auth";
    /**
     * 错误码：服务触发qps限流
     */
    public static final String ERROR_SERVICE_LIMIT = "ERROR_GATEWAY_LIMIT";
    /**
     * 错误信息：服务触发qps限流
     */
    public static final String ERROR_MSG_SERVICE_LIMIT = "当前访问人数过多,请稍后尝试";
}
