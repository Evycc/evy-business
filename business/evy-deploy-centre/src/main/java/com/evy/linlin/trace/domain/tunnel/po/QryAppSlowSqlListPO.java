package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:41
 */
@Getter
public class QryAppSlowSqlListPO {
    private String tssReqIp;
    private String tssSlowSql;
    private String tssTakeTime;
    private String tssExplain;
    private String tssExplainContent;
    private String gmtModify;
}
