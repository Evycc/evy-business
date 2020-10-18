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
    private String appIp;
    private String slowSql;
    private String takeTime;
    private String explain;
    private String explainContent;
    private String gmtModify;
}
