package com.evy.linlin.trace.domain.repository;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.domain.tunnel.constant.QryTraceErrorConstant;
import com.evy.linlin.trace.domain.tunnel.model.*;
import com.evy.linlin.trace.domain.tunnel.po.*;
import com.evy.linlin.trace.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 获取监控信息(内存、redis、线程...)
 *
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:11
 */
@Repository
public class QryTraceInfoRepository {
    private final QryTraceInfoDataRepository dataRepository;

    public QryTraceInfoRepository(QryTraceInfoDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * 根据部署序列号获取对应机器IP
     *
     * @param getAppIpFromUserSeqDO com.evy.linlin.trace.domain.repository.tunnel.model.GetAppIpFromUserSeqDO
     * @return 返回对应机器IP列表
     */
    public List<String> getAppIpFromUserSeq(GetAppIpFromUserSeqDO getAppIpFromUserSeqDO) {
        GetTargetIpFromSeqPO getTargetIpFromSeqPo = QryTraceAssembler.doConvertPo(getAppIpFromUserSeqDO);
        String targetIp = dataRepository.getTargetIpFromSeq(getTargetIpFromSeqPo);
        List<String> list;

        if (!StringUtils.isEmpty(targetIp) && targetIp.contains(BusinessConstant.COMMA)) {
            list = Arrays.asList(targetIp.split(BusinessConstant.COMMA, -1));
        } else {
            list = new ArrayList<>(1);
            list.add(targetIp);
        }

        return list;
    }

    /**
     * 查询目标服务器IP
     *
     * @param seq     部署唯一流水
     * @param userSeq 用户标识
     * @return 返回对应机器IP列表
     */
    private List<String> getTargetIp(String seq, String userSeq) {
        GetAppIpFromUserSeqDO getAppIpFromUserSeqDo = QryTraceAssembler.createGetAppIpFromUserSeqDO(seq, userSeq);
        return getAppIpFromUserSeq(getAppIpFromUserSeqDo);
    }

    /**
     * 查询应用服务器内存信息集合
     *
     * @param qryAppMermoryListDo com.evy.linlin.trace.domain.repository.tunnel.model.QryAppMermoryListDO
     * @return com.evy.linlin.trace.dto.QryAppMermoryInfoModel
     */
    public List<QryAppMermoryInfoModel> qryAppMermoryInfoList(QryAppMermoryInfoListDO qryAppMermoryListDo) {
        List<QryAppMermoryInfoModel> result = new ArrayList<>();
        List<String> targetIpList = getTargetIp(qryAppMermoryListDo.getSeq(), qryAppMermoryListDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            targetIpList.stream()
                    .map(targetIp -> dataRepository.qryAppMermoryList(
                            QryTraceAssembler.createQryAppMermoryPO(targetIp, qryAppMermoryListDo))
                    )
                    .map(QryTraceAssembler::createQryAppMermoryInfoModel)
                    .filter(models -> !CollectionUtils.isEmpty(models))
                    .reduce((list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    })
                    .ifPresent(result::addAll);
        }

        return result;
    }

    /**
     * 查询应用Http请求信息
     *
     * @param httpReqInfoListDo com.evy.linlin.trace.domain.repository.tunnel.model.qryHttpReqInfoListDO
     * @return com.evy.linlin.trace.dto.QryHttpInfoModel
     */
    public List<QryHttpInfoModel> qryHttpReqInfoList(QryHttpReqInfoListDO httpReqInfoListDo) {
        List<QryHttpInfoModel> result = new ArrayList<>();
        List<String> targetIpList = getTargetIp(httpReqInfoListDo.getSeq(), httpReqInfoListDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            targetIpList.stream()
                    .map(targetIp -> dataRepository.qryAppHttpReqInfoList(QryTraceAssembler.createQryAppHttpReqPO(targetIp, httpReqInfoListDo)))
                    .map(qryAppHttpReqListPos -> {
                        String path = httpReqInfoListDo.getPath();
                        if (StringUtils.isEmpty(path)) {
                            return qryAppHttpReqListPos;
                        } else {
                            List<QryAppHttpReqListPO> temp = new ArrayList<>();
                            for (QryAppHttpReqListPO qryAppHttpReqListPo : qryAppHttpReqListPos) {
                                if (qryAppHttpReqListPo.getThfUrl().contains(path)) {
                                    temp.add(qryAppHttpReqListPo);
                                }
                            }
                            return temp;
                        }
                    })
                    .map(QryTraceAssembler::createQryHttpInfoModel)
                    .filter(models -> !CollectionUtils.isEmpty(models))
                    .reduce((list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    })
                    .ifPresent(result::addAll);
        }

        return result;
    }

    /**
     * 查询MQ发布消费情况
     *
     * @param qryMqTraceInfoListDO com.evy.linlin.trace.domain.repository.tunnel.model.QryMqTraceInfoListDO
     * @return com.evy.linlin.trace.dto.QryMqTraceInfoModel
     */
    public List<QryMqTraceInfoModel> qryMqTraceInfoList(QryMqTraceInfoListDO qryMqTraceInfoListDO) {
        List<QryMqTraceInfoModel> result = null;
        List<String> targetIpList = getTargetIp(qryMqTraceInfoListDO.getSeq(), qryMqTraceInfoListDO.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            List<QryMqTraceInfoListPO> pos = dataRepository.qryMqTraceInfoList(
                    QryTraceAssembler.createQryMqTraceInfoPO(targetIpList, qryMqTraceInfoListDO));
            result = QryTraceAssembler.createQryMqTraceInfoModel(pos);
        }

        return result;
    }

    /**
     * 查询Redis服务器健康信息
     *
     * @param qryRedisInfoDo com.evy.linlin.trace.domain.repository.tunnel.model.QryRedisInfoDO
     * @return com.evy.linlin.trace.dto.QryRedisInfoModel
     */
    public List<QryRedisInfoModel> qryRedisInfo(QryRedisInfoDO qryRedisInfoDo) {
        List<QryRedisInfoModel> result = null;
        List<String> targetIpList = getTargetIp(qryRedisInfoDo.getBuildSeq(), qryRedisInfoDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            result = targetIpList.stream()
                    .map(targetIp -> dataRepository.qryRedisInfo(QryTraceAssembler.createQryRedisInfoPO(targetIp)))
                    .map(QryTraceAssembler::createQryRedisInfoModel)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return result;
    }

    /**
     * 查询应用服务发布信息
     *
     * @param qryAppServiceInfoListDo com.evy.linlin.trace.domain.repository.tunnel.model.QryAppServiceInfoListDO
     * @return com.evy.linlin.trace.dto.QryServiceInfoModel
     */
    public List<QryServiceInfoModel> qryAppServiceInfoList(QryAppServiceInfoListDO qryAppServiceInfoListDo) {
        List<QryServiceInfoModel> result = null;
        List<String> targetIpList = getTargetIp(qryAppServiceInfoListDo.getBuildSeq(), qryAppServiceInfoListDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            List<QryServiceInfoListPO> pos = dataRepository.qryServiceInfoList(QryTraceAssembler.createQryServiceInfoPO(targetIpList, qryAppServiceInfoListDo));
            result = QryTraceAssembler.createQryServiceInfoModel(pos);
        }

        return qrySrvLimitInfo(result);
    }

    /**
     * 查询服务对应限流信息
     * @param models com.evy.linlin.trace.dto.QryServiceInfoModel
     * @return com.evy.linlin.trace.dto.QryServiceInfoModel
     */
    public List<QryServiceInfoModel> qrySrvLimitInfo(List<QryServiceInfoModel> models) {
        if (!CollectionUtils.isEmpty(models)) {
            models.forEach(model -> {
                QrySrvLimitInfoOutPO outPo = dataRepository.qrySrvLimitInfo(QryTraceAssembler.createQrySrvLimitInfoPO(model));
                if (Objects.nonNull(outPo)) {
                    model.setLimitQps(outPo.getQpsLimit());
                    model.setLimitFallback(outPo.getFallback());
                }
            });
        }

        return models;
    }

    /**
     * 查询应用慢SQL情况
     *
     * @param qryAppSlowSqlListDo com.evy.linlin.trace.domain.repository.tunnel.model.qryAppSlowSqlListDO
     * @return com.evy.linlin.trace.dto.QrySlowSqlInfoModel
     */
    public List<QrySlowSqlInfoModel> qryAppSlowSqlList(QryAppSlowSqlListDO qryAppSlowSqlListDo) {
        List<QrySlowSqlInfoModel> result = null;
        List<String> targetIpList = getTargetIp(qryAppSlowSqlListDo.getBuildSeq(), qryAppSlowSqlListDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            List<QryAppSlowSqlListPO> slowSqlListPos = dataRepository.qryAppSlowSqlList(QryTraceAssembler.createQryAppSlowSqlListPO(targetIpList));
            result = QryTraceAssembler.createQrySlowSqlInfoModel(slowSqlListPos);
        }

        return result;
    }

    /**
     * 查询应用线程信息集合
     *
     * @param qryAppThreadInfoListDo com.evy.linlin.trace.domain.repository.tunnel.model.QryAppThreadInfoListDO
     * @return com.evy.linlin.trace.dto.QryThreadsInfoModel
     */
    public List<QryThreadsInfoModel> qryAppThreadInfoList(QryAppThreadInfoListDO qryAppThreadInfoListDo) {
        List<QryThreadsInfoModel> result = new ArrayList<>();
        List<String> targetIpList = getTargetIp(qryAppThreadInfoListDo.getBuildSeq(), qryAppThreadInfoListDo.getUserSeq());
        if (!CollectionUtils.isEmpty(targetIpList)) {
            targetIpList.stream()
                    .filter(targetIp -> qryAppThreadInfoListDo.getServiceIp().equals(targetIp))
                    .map(targetIp -> dataRepository.qryAppThreadInfoList(QryTraceAssembler.createQryAppThreadInfoPO(targetIp, qryAppThreadInfoListDo)))
                    .map(QryTraceAssembler::createQryThreadsInfoModel)
                    .filter(models -> !CollectionUtils.isEmpty(models))
                    .reduce((list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    })
                    .ifPresent(result::addAll);
        }

        return result;
    }

    /**
     * 新增服务码
     * @param infoDo com.evy.linlin.trace.domain.tunnel.model.CreateNewSrvInfoDo
     * @return true: 新增成功
     */
    public boolean createNewSrvInfo(CreateNewSrvInfoDo infoDo) throws BasicException {
        boolean result = false;
        SrvInfoPO po = QryTraceAssembler.doConvertPo(infoDo);
        if (dataRepository.hasSrvInfo(po)) {
            throw new BasicException(QryTraceErrorConstant.SRV_EXIST_ERROR_CODE, QryTraceErrorConstant.SRV_EXIST_ERROR_MSG);
        } else {
            //不存在服务码
            result = dataRepository.createSrvInfo(po);
        }

        return result;
    }

    /**
     * 更新服务码或限流信息
     * @param infoDo com.evy.linlin.trace.domain.tunnel.model.ModifySrvInfoDo
     * @return true: 更新成功
     */
    public boolean modifySrvInfo(ModifySrvInfoDo infoDo) {
        SrvInfoPO po = QryTraceAssembler.doConvertPo(infoDo);

        return dataRepository.createSrvInfo(po) && dataRepository.createSrvLimitInfo(po);
    }

    /**
     * 伪分段返回
     *
     * @param list       原list
     * @param beginIndex 忽略的行数
     * @param endIndex   返回总记录数
     * @return list
     */
    public <T> List<T> skipListResult(List<T> list, Integer beginIndex, Integer endIndex) {
        if (Objects.isNull(beginIndex)) {
            beginIndex = 0;
        }
        if (Objects.isNull(endIndex)) {
            endIndex = 500;
        }
        return list.stream()
                .skip(beginIndex)
                .limit(endIndex)
                .collect(Collectors.toList());
    }
}
