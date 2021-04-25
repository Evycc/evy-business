package com.evy.linlin.trace.domain.repository;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.linlin.trace.domain.tunnel.po.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:41
 */
@Repository
public class QryTraceInfoDataRepository {
    private final static String QRY_TARGET_IP_FROM_SEQ_SQL = "QryTraceInfoMapper.getTargetIpFromSeq";
    private final static String QRY_APP_MERMORY_LIST_SQL = "QryTraceInfoMapper.qryAppMermoryList";
    private final static String QRY_APP_THREAD_INFO_LIST_SQL = "QryTraceInfoMapper.qryAppThreadInfoList";
    private final static String QRY_HTTP_REQ_INFO_LIST_SQL = "QryTraceInfoMapper.qryAppHttpReqInfoList";
    private final static String QRY_MQ_INFO_LIST_SQL = "QryTraceInfoMapper.qryMqTraceInfoList";
    private final static String QRY_REDIS_INFO_SQL = "QryTraceInfoMapper.qryRedisInfo";
    private final static String QRY_SERVICE_INFO_LIST_SQL = "QryTraceInfoMapper.qryServiceInfoList";
    private final static String QRY_SERVICE_LIMIT_INFO_SQL = "QryTraceInfoMapper.qrySrvLimitInfo";
    private final static String QRY_SLOW_SQL_LIST_SQL = "QryTraceInfoMapper.qryAppSlowSqlList";
    private final static String ADD_NEW_SRV_INFO_SQL = "QryTraceInfoMapper.addSrvInfo";
    private final static String QRY_SRV_INFO_SQL = "QryTraceInfoMapper.qrySrvCodeOne";
    private final static String ADD_SRV_LIMIT_INFO_SQL = "QryTraceInfoMapper.addSrvLimitInfo";

    /**
     * 查询序列对应目标服务器IP列表
     * @param po    com.evy.linlin.trace.domain.repository.tunnel.po.GetTargetIpFromSeqPO
     * @return 返回目标服务器IP, || 分割
     */
    public String getTargetIpFromSeq(GetTargetIpFromSeqPO po) {
        return DBUtils.selectOne(QRY_TARGET_IP_FROM_SEQ_SQL, po);
    }

    /**
     * 查询服务器内存信息集合
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryAppMermoryPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.QryAppMermoryListPO
     */
    public List<QryAppMermoryListPO> qryAppMermoryList(QryAppMermoryPO po) {
        return DBUtils.selectList(QRY_APP_MERMORY_LIST_SQL, po);
    }

    /**
     * 查询应用线程信息集合
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryAppThreadInfoPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.QryAppThreadInfoListPO
     */
    public List<QryAppThreadInfoListPO> qryAppThreadInfoList(QryAppThreadInfoPO po) {
        return DBUtils.selectList(QRY_APP_THREAD_INFO_LIST_SQL, po);
    }

    /**
     * 查询应用http请求记录集合
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryAppHttpReqPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.QryAppHttpReqListPO
     */
    public List<QryAppHttpReqListPO> qryAppHttpReqInfoList(QryAppHttpReqPO po) {
        return DBUtils.selectList(QRY_HTTP_REQ_INFO_LIST_SQL, po);
    }

    /**
     * 查询MQ轨迹
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryMqTraceInfoPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.QryMqTraceInfoListPO
     */
    public List<QryMqTraceInfoListPO> qryMqTraceInfoList(QryMqTraceInfoPO po) {
        return DBUtils.selectList(QRY_MQ_INFO_LIST_SQL, po);
    }

    /**
     * 查询Redis服务器健康信息
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryRedisInfoPO
     * @return com.evy.linlin.trace.domain.repository.tunnel.po.QryRedisInfoOutPO
     */
    public List<QryRedisInfoOutPO> qryRedisInfo(QryRedisInfoPO po) {
        return DBUtils.selectList(QRY_REDIS_INFO_SQL, po);
    }

    /**
     * 查询应用服务发布信息集合
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.qryServiceInfoPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.qryServiceInfoListPO
     */
    public List<QryServiceInfoListPO> qryServiceInfoList(QryServiceInfoPO po) {
        return DBUtils.selectList(QRY_SERVICE_INFO_LIST_SQL, po);
    }

    /**
     * 查询服务对应限流信息
     * @param po com.evy.linlin.trace.domain.tunnel.po.QrySrvLimitInfoPO
     * @return com.evy.linlin.trace.domain.tunnel.po.QrySrvLimitInfoOutPO
     */
    public QrySrvLimitInfoOutPO qrySrvLimitInfo(QrySrvLimitInfoPO po) {
        return DBUtils.selectOne(QRY_SERVICE_LIMIT_INFO_SQL, po);
    }

    /**
     * 查询应用慢SQL记录集合
     * @param po com.evy.linlin.trace.domain.repository.tunnel.po.QryAppSlowSqlPO
     * @return List集合 com.evy.linlin.trace.domain.repository.tunnel.po.QryAppSlowSqlListPO
     */
    public List<QryAppSlowSqlListPO> qryAppSlowSqlList(QryAppSlowSqlPO po) {
        return DBUtils.selectList(QRY_SLOW_SQL_LIST_SQL, po);
    }

    /**
     * 查询对应服务码是否存在
     * @param po srvCode
     * @return true:存在 false:不存在
     */
    public boolean hasSrvInfo(SrvInfoPO po) {
        Integer result = DBUtils.selectOne(QRY_SRV_INFO_SQL, po);
        return result == BusinessConstant.ONE_NUM;
    }

    /**
     * 新增服务码
     * @param po com.evy.linlin.trace.domain.tunnel.po.SrvInfoPO
     * @return true:新增成功
     */
    public boolean createSrvInfo(SrvInfoPO po) {
        return DBUtils.insert(ADD_NEW_SRV_INFO_SQL, po) == BusinessConstant.ONE_NUM;
    }

    /**
     * 新增服务限流信息
     * @param po com.evy.linlin.trace.domain.tunnel.po.SrvInfoPO
     * @return true:新增成功
     */
    public boolean createSrvLimitInfo(SrvInfoPO po) {
        return DBUtils.insert(ADD_SRV_LIMIT_INFO_SQL, po) == BusinessConstant.ONE_NUM;
    }
}
