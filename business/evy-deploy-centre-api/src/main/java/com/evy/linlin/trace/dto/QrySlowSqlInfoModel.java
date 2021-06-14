package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:14
 */
public class QrySlowSqlInfoModel extends CommandModel {
    /**
     * 请求的服务器IP
     */
    private String appIp;
    /**
     * 慢sql
     */
    private String slowSql;
    /**
     * sql耗时
     */
    private String takeTime;
    /**
     * sql explain完整语句
     */
    private String explain;
    /**
     * sql explain优化建议
     */
    private String explainContent;
    /**
     * 最后记录时间
     */
    private String gmtModify;

    public QrySlowSqlInfoModel() {
    }

    public QrySlowSqlInfoModel(String appIp, String slowSql, String takeTime, String explain, String explainContent, String gmtModify) {
        this.appIp = appIp;
        this.slowSql = slowSql;
        this.takeTime = takeTime;
        this.explain = explain;
        this.explainContent = explainContent;
        this.gmtModify = gmtModify;
    }

    public void setAppIp(String appIp) {
        this.appIp = appIp;
    }

    public void setSlowSql(String slowSql) {
        this.slowSql = slowSql;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setExplainContent(String explainContent) {
        this.explainContent = explainContent;
    }

    public void setGmtModify(String gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getAppIp() {
        return appIp;
    }

    public String getSlowSql() {
        return slowSql;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public String getExplain() {
        return explain;
    }

    public String getExplainContent() {
        return explainContent;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    @Override
    public String toString() {
        return "QrySlowSqlInfoModel{" +
                "appIp='" + appIp + '\'' +
                ", slowSql='" + slowSql + '\'' +
                ", takeTime='" + takeTime + '\'' +
                ", explain='" + explain + '\'' +
                ", explainContent='" + explainContent + '\'' +
                ", gmtModify='" + gmtModify + '\'' +
                '}';
    }
}
