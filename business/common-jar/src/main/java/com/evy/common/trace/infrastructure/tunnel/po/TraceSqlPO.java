package com.evy.common.trace.infrastructure.tunnel.po;

import com.evy.common.command.infrastructure.constant.BusinessConstant;

/**
 * @Author: EvyLiuu
 * @Date: 2020/6/20 10:06
 */
public class TraceSqlPO {
    private final String tssReqIp;
    private final String tssSlowSql;
    private final String tssTakeTime;
    private final String tssExplain;
    private final String tssExplainContent;

    private TraceSqlPO(String tssReqIp, String tssSlowSql, String tssTakeTime, String tssExplain, String tssExplainContent) {
        this.tssReqIp = tssReqIp;
        this.tssSlowSql = tssSlowSql;
        this.tssTakeTime = tssTakeTime;
        this.tssExplain = tssExplain;
        this.tssExplainContent = tssExplainContent;
    }

    /**
     * 隐藏构造方法细节,全量参数构造方法
     */
    public static TraceSqlPO create(String tssReqIp, String tssSlowSql, String tssTakeTime, String tssExplain, String tssExplainContent) {
        int limit1 = 1200;
        int limit2 = 1000;
        if (tssSlowSql.length() > limit2) {
            tssSlowSql = tssSlowSql.substring(0, limit2);
        }
        if (tssExplainContent.length() > limit2) {
            tssExplainContent = tssSlowSql.substring(0, limit2);
        }
        if (tssExplain.length() > limit1) {
            tssExplain = tssExplain.substring(0, limit1);
        }
        return new TraceSqlPO(tssReqIp, tssSlowSql, tssTakeTime, tssExplain, tssExplainContent);
    }

    /**
     * 隐藏构造方法细节,构造非explain类型
     */
    public static TraceSqlPO create(String tssReqIp, String tssSlowSql, String tssTakeTime) {
        return new TraceSqlPO(tssReqIp, tssSlowSql, tssTakeTime, BusinessConstant.EMPTY_STR, BusinessConstant.EMPTY_STR);
    }

    @Override
    public String toString() {
        return "TraceSqlPO{" +
                "tssReqIp='" + tssReqIp + '\'' +
                ", tssSlowSql='" + tssSlowSql + '\'' +
                ", tssTakeTime='" + tssTakeTime + '\'' +
                ", tssExplain='" + tssExplain + '\'' +
                ", tssExplainContent='" + tssExplainContent + '\'' +
                '}';
    }

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
}
