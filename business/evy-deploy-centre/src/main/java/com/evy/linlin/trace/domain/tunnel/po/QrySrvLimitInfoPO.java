package com.evy.linlin.trace.domain.tunnel.po;

/**
 * @Author: EvyLiuu
 * @Date: 2021/3/13 16:35
 */
public class QrySrvLimitInfoPO {
    private String srvCode;
    private String srvName;

    public QrySrvLimitInfoPO(String srvCode, String srvName) {
        this.srvCode = srvCode;
        this.srvName = srvName;
    }

    public String getSrvCode() {
        return srvCode;
    }

    public String getSrvName() {
        return srvName;
    }

    @Override
    public String toString() {
        return "QrySrvLimitInfoPO{" +
                "srvCode='" + srvCode + '\'' +
                ", srvName='" + srvName + '\'' +
                '}';
    }
}
