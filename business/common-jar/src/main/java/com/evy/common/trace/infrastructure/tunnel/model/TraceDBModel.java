package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * @Author: EvyLiuu
 * @Date: 2020/6/7 20:25
 */
public class TraceDBModel extends TraceModel {
    private String slowSql;
    private String explain;
    private String explainContent;

    public TraceDBModel(String reqIp, long takeUpTimestamp, String slowSql, String explain, String explainContent) {
        super(reqIp, takeUpTimestamp);
        this.slowSql = slowSql;
        this.explain = explain;
        this.explainContent = explainContent;
    }

    public static TraceDBModel create(String reqIp, long takeUpTimestamp, String slowSql, String explain, String explainContent) {
        return new TraceDBModel(reqIp, takeUpTimestamp, slowSql, explain, explainContent);
    }

    public static TraceDBModel create(String reqIp, long takeUpTimestamp, String slowSql) {
        return new TraceDBModel(reqIp, takeUpTimestamp, slowSql, null, null);
    }

    @Override
    public String toString() {
        return "TraceDBModel{" +
                "slowSql='" + slowSql + '\'' +
                ", explain='" + explain + '\'' +
                ", explainContent='" + explainContent + '\'' +
                '}';
    }

    public String getSlowSql() {
        return slowSql;
    }

    public String getExplain() {
        return explain;
    }

    public String getExplainContent() {
        return explainContent;
    }
}
