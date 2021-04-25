package com.evy.linlin.gateway.filter.repository;

import com.evy.common.database.DBUtils;
import com.evy.linlin.gateway.filter.repository.po.ServiceInfoPO;
import com.evy.linlin.gateway.filter.repository.po.ServiceLimitInfoPO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * com.evy.linlin.gateway.filter.ServiceFilter Repository
 * @Author: EvyLiuu
 * @Date: 2020/7/25 17:45
 */
@Repository
public class ServiceFilterRepository {
    private static final String QUERY_SERVICE_CONSUMERS = "com.evy.linlin.gateway.filter.repository.mapper.ServiceMapper.queryServiceAndConsumer";
    private static final String QUERY_SERVICE_LIMIT_INFOS = "com.evy.linlin.gateway.filter.repository.mapper.ServiceMapper.queryServiceLimitInfo";

    /**
     * 查询服务码及对应消费者信息
     * @return com.evy.linlin.gateway.filter.repository.po.ServiceInfoPO
     */
    public List<ServiceInfoPO> queryServiceAndConsumers() {
        return DBUtils.selectList(QUERY_SERVICE_CONSUMERS);
    }

    /**
     * 查询服务限流信息
     * @return com.evy.linlin.gateway.filter.repository.po.ServiceLimitInfoPO
     */
    public List<ServiceLimitInfoPO> queryServiceLimitInfos() {
        return DBUtils.selectList(QUERY_SERVICE_LIMIT_INFOS);
    }
}
