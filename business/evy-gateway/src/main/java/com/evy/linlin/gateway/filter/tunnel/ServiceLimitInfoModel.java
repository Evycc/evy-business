package com.evy.linlin.gateway.filter.tunnel;

import com.evy.common.utils.DateUtils;
import com.evy.linlin.gateway.filter.repository.po.ServiceLimitInfoPO;

import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: EvyLiuu
 * @Date: 2020/7/26 1:40
 */
public class ServiceLimitInfoModel {
    private String serviceBeanName;
    private int serviceQpsLimit;
    private String serviceFallback;
    private long lastTimestamp = -1;
    private AtomicInteger tempQpsLimit = new AtomicInteger();

    private ServiceLimitInfoModel(String serviceBeanName, int serviceQpsLimit, String serviceFallback) {
        this.serviceBeanName = serviceBeanName;
        this.serviceQpsLimit = serviceQpsLimit;
        this.serviceFallback = serviceFallback;
    }

    public static ServiceLimitInfoModel convert(ServiceLimitInfoPO serviceLimitInfoPo){
        return new ServiceLimitInfoModel(serviceLimitInfoPo.getSliServiceBeanName(), serviceLimitInfoPo.getSliQpsLimit(),
                serviceLimitInfoPo.getSliFallback());
    }

    /**
     * 尝试执行请求
     * @return false : 触发qps限流 true : 限流通过
     */
    public synchronized boolean tryExecute() {
        long timestamp = getTimestamp();
        boolean result = false;
        if (lastTimestamp == timestamp) {
            //同一秒内请求
            if (tempQpsLimit.incrementAndGet() < serviceQpsLimit) {
                result = true;
            }
        } else {
            //非同一秒内请求,允许通过
            result = true;
            tempQpsLimit.set(0);
        }

        lastTimestamp = timestamp;
        return result;
    }

    /**
     * 获取当前时间,精确到秒
     * @return  精确到秒的时间戳
     */
    private synchronized long getTimestamp(){
        return DateUtils.parse(DateUtils.nowStr1(), DateUtils.YYYY_MM_DD_HH_MM_SS).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    public int getServiceQpsLimit() {
        return serviceQpsLimit;
    }

    public String getServiceFallback() {
        return serviceFallback;
    }
}
