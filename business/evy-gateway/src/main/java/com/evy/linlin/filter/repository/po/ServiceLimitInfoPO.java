package com.evy.linlin.filter.repository.po;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/25 22:11
 */
public class ServiceLimitInfoPO {
    private String sliServiceBeanName;
    private int sliQpsLimit;
    private String sliFallback;

    public String getSliServiceBeanName() {
        return sliServiceBeanName;
    }

    public void setSliServiceBeanName(String sliServiceBeanName) {
        this.sliServiceBeanName = sliServiceBeanName;
    }

    public int getSliQpsLimit() {
        return sliQpsLimit;
    }

    public void setSliQpsLimit(int sliQpsLimit) {
        this.sliQpsLimit = sliQpsLimit;
    }

    public String getSliFallback() {
        return sliFallback;
    }

    public void setSliFallback(String sliFallback) {
        this.sliFallback = sliFallback;
    }
}
