package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 15:41
 */
public class QryAppSlowSqlListPO {
    private String tssReqIp;
    private String tssSlowSql;
    private String tssTakeTime;
    private String tssExplain;
    private String tssExplainContent;
    private String gmtModify;

    public String getTssReqIp() {
        return tssReqIp;
    }

    public String getTssSlowSql() {
        return tssSlowSql;
    }

    public String getTssTakeTime() {
        return tssTakeTime;
    }

    public String getTssExplain() {
        return tssExplain;
    }

    public String getTssExplainContent() {
        return tssExplainContent;
    }

    public String getGmtModify() {
        return gmtModify;
    }
}
