package com.evy.common.trace.infrastructure.tunnel.model;

/**
 * 内存信息模型
 * @Author: EvyLiuu
 * @Date: 2021/4/4 21:38
 */
public class HeadDumpInfoModel {
    /**
     * dump文件路径
     */
    private String dumpFilePath;
    /**
     * dump结果 0成功 1失败
     */
    private Integer dumpResult;
    /**
     * dump结果错误信息
     */
    private String dumpResultErrorText;

    public HeadDumpInfoModel() {
    }

    public HeadDumpInfoModel(String dumpFilePath, Integer dumpResult, String dumpResultErrorText) {
        this.dumpFilePath = dumpFilePath;
        this.dumpResult = dumpResult;
        this.dumpResultErrorText = dumpResultErrorText;
    }

    public String getDumpFilePath() {
        return dumpFilePath;
    }

    public void setDumpFilePath(String dumpFilePath) {
        this.dumpFilePath = dumpFilePath;
    }

    public Integer getDumpResult() {
        return dumpResult;
    }

    public void setDumpResult(Integer dumpResult) {
        this.dumpResult = dumpResult;
    }

    public String getDumpResultErrorText() {
        return dumpResultErrorText;
    }

    public void setDumpResultErrorText(String dumpResultErrorText) {
        this.dumpResultErrorText = dumpResultErrorText;
    }

    @Override
    public String toString() {
        return "HeadDumpInfoModel{" +
                "dumpFilePath='" + dumpFilePath + '\'' +
                ", dumpResult=" + dumpResult +
                ", dumpResultErrorText='" + dumpResultErrorText + '\'' +
                '}';
    }
}
