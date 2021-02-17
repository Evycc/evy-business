package com.evy.linlin.trace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:14
 */
@ToString
@AllArgsConstructor
@Getter
public class QrySlowSqlInfoModel {
    /**
     * 请求的服务器IP
     */
    private final String appIp;
    /**
     * 慢sql
     */
    private final String slowSql;
    /**
     * sql耗时
     */
    private final String takeTime;
    /**
     * sql explain完整语句
     */
    private final String explain;
    /**
     * sql explain优化建议
     */
    private final String explainContent;
    /**
     * 最后记录时间
     */
    private final String gmtModify;
}
