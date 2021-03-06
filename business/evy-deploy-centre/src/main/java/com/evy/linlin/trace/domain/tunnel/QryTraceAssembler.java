package com.evy.linlin.trace.domain.tunnel;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.linlin.trace.domain.tunnel.constant.QryTraceErrorConstant;
import com.evy.linlin.trace.domain.tunnel.model.*;
import com.evy.linlin.trace.domain.tunnel.po.*;
import com.evy.linlin.trace.dto.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:29
 */
public class QryTraceAssembler {
    /*---------------- doConvertDto DTO转DO ----------------*/

    /**
     * QryThreadsInfoDTO -> QryAppThreadInfoListDO
     *
     * @param dto com.evy.linlin.trace.dto.QryThreadsInfoDTO
     * @return com.evy.linlin.trace.domain.repository.tunnel.model.QryAppThreadInfoListDO
     */
    public static QryAppThreadInfoListDO dtoConvertDo(QryThreadsInfoDTO dto) {
        return new QryAppThreadInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getThreadName(),
                dto.getBeginIndex(), dto.getEndIndex(), dto.getServiceIp());
    }

    /**
     * QrySlowSqlInfoDTO -> QryAppSlowSqlListDO
     *
     * @param dto com.evy.linlin.trace.dto.QrySlowSqlInfoDTO
     * @return com.evy.linlin.trace.domain.repository.tunnel.model.QryAppSlowSqlListDO
     */
    public static QryAppSlowSqlListDO dtoConvertDo(QrySlowSqlInfoDTO dto) {
        int limit = BusinessConstant.ZERO_NUM;
        if (!StringUtils.isEmpty(dto.getLimit())) {
            limit = Integer.parseInt(dto.getLimit());
        }
        return new QryAppSlowSqlListDO(dto.getBuildSeq(), dto.getUserSeq(), limit);
    }

    /**
     * QryAppMermoryInfoDTO -> QryAppMermoryListDO
     *
     * @param dto com.evy.linlin.trace.dto.QryAppMermoryInfoDTO
     * @return com.evy.linlin.trace.domain.repository.tunnel.model.QryAppMermoryListDO
     */
    public static QryAppMermoryInfoListDO dtoConvertDo(QryAppMermoryInfoDTO dto) {
        int limit = BusinessConstant.ZERO_NUM;
        if (!StringUtils.isEmpty(dto.getLimit())) {
            limit = Integer.parseInt(dto.getLimit());
        }
        return new QryAppMermoryInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), limit);
    }

    /**
     * QryHttpInfoDTO -> qryHttpReqInfoListDO
     */
    public static QryHttpReqInfoListDO dtoConvertDo(QryHttpInfoDTO dto) {
        int limit = BusinessConstant.ZERO_NUM;
        if (!StringUtils.isEmpty(dto.getLimit())) {
            limit = Integer.parseInt(dto.getLimit());
        }
        return new QryHttpReqInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getPath(), limit);
    }

    /**
     * QryMqTraceInfoDTO -> QryMqTraceInfoListDO
     */
    public static QryMqTraceInfoListDO dtoConvertDo(QryMqTraceInfoDTO dto) {
        int limit = BusinessConstant.ZERO_NUM;
        if (!StringUtils.isEmpty(dto.getLimit())) {
            limit = Integer.parseInt(dto.getLimit());
        }
        return new QryMqTraceInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getTopic(), dto.getMsgId(), limit);
    }

    /**
     * QryRedisInfoDTO -> QryRedisInfoDO
     */
    public static QryRedisInfoDO dtoConvertDo(QryRedisInfoDTO dto) {
        return new QryRedisInfoDO(dto.getBuildSeq(), dto.getUserSeq());
    }

    /**
     * QryServiceInfoDTO -> QryAppServiceInfoListDO
     */
    public static QryAppServiceInfoListDO dtoConvertDo(QryServiceInfoDTO dto) {
        return new QryAppServiceInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getServiceName());
    }

    /*---------------- doConvertPo DO转PO ----------------*/

    /**
     * 创建PO GetTargetIpFromSeqPO
     *
     * @param inputDo com.evy.linlin.trace.domain.repository.tunnel.model.GetAppIpFromUserSeqDO
     * @return com.evy.linlin.trace.domain.repository.tunnel.po.GetTargetIpFromSeqPO
     */
    public static GetTargetIpFromSeqPO doConvertPo(GetAppIpFromUserSeqDO inputDo) {
        return new GetTargetIpFromSeqPO(inputDo.getSeq(), inputDo.getUserSeq());
    }

    /*---------------- create 创建实例 ----------------*/

    /**
     * 创建实例 : GetAppIpFromUserSeqDO
     */
    public static GetAppIpFromUserSeqDO createGetAppIpFromUserSeqDO(String seq, String userSeq) {
        return new GetAppIpFromUserSeqDO(seq, userSeq);
    }

    /**
     * 创建实例 : QryAppMermoryPO
     *
     * @param targetIp                目标服务器IP
     * @param qryAppMermoryInfoListDo com.evy.linlin.trace.domain.repository.tunnel.model.QryAppMermoryInfoListDO
     * @return com.evy.linlin.trace.domain.repository.tunnel.po.QryAppMermoryPO
     */
    public static QryAppMermoryPO createQryAppMermoryPO(String targetIp, QryAppMermoryInfoListDO qryAppMermoryInfoListDo) {
        int limit = qryAppMermoryInfoListDo.getLimit();
        limit = Math.max(limit, 7);
        return new QryAppMermoryPO(targetIp, limit);
    }

    /**
     * List<QryAppMermoryListPO> -> List<QryAppMermoryInfoModel>
     *
     * @param pos com.evy.linlin.trace.domain.repository.tunnel.po.QryAppMermoryListPO
     * @return com.evy.linlin.trace.dto.QryAppMermoryInfoModel
     */
    public static List<QryAppMermoryInfoModel> createQryAppMermoryInfoModel(List<QryAppMermoryListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryAppMermoryListPo -> new QryAppMermoryInfoModel(
                        qryAppMermoryListPo.getTamiAppIp(), qryAppMermoryListPo.getTamiCpuCount(), qryAppMermoryListPo.getTamiCpuLoad(),
                        qryAppMermoryListPo.getTamiSysMemory(), qryAppMermoryListPo.getTamiSysAvailMemory(), qryAppMermoryListPo.getTamiAppUseMemory(),
                        qryAppMermoryListPo.getTamiAppHeapMaxMemory(), qryAppMermoryListPo.getTamiAppHeapMinMemory(), qryAppMermoryListPo.getTamiAppHeapUseMemory(),
                        qryAppMermoryListPo.getTamiAppNoheapMaxMemory(), qryAppMermoryListPo.getTamiAppNoheapMinMemory(), qryAppMermoryListPo.getTamiAppNoheapUseMemory(),
                        qryAppMermoryListPo.getGmtModify()
                ))
                .collect(Collectors.toList());
    }

    /**
     * List<QryServiceInfoListPO> -> List<QryServiceInfoModel>
     *
     * @param pos com.evy.linlin.trace.domain.repository.tunnel.po.QryServiceInfoListPO
     * @return com.evy.linlin.trace.dto.QryServiceInfoModel
     */
    public static List<QryServiceInfoModel> createQryServiceInfoModel(List<QryServiceInfoListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryServiceInfoListPo -> {
                    List<String> consumers = null;
                    List<String> providerNames = null;
                    List<String> consumerNames = null;
                    String consumer = qryServiceInfoListPo.getTsiConsumer();
                    String providerName = qryServiceInfoListPo.getTsiProviderNames();
                    String consumerName = qryServiceInfoListPo.getTsiConsumerNames();
                    if (!StringUtils.isEmpty(consumer)) {
                        consumers = Arrays.stream(consumer.split(BusinessConstant.SPLIT_LINE, -1)).collect(Collectors.toList());
                    }
                    if (!StringUtils.isEmpty(providerName)) {
                        providerNames = Arrays.stream(providerName.split(BusinessConstant.SPLIT_LINE, -1)).collect(Collectors.toList());
                    }
                    if (!StringUtils.isEmpty(consumerName)) {
                        consumerNames = Arrays.stream(consumerName.split(BusinessConstant.SPLIT_LINE, -1)).collect(Collectors.toList());
                    }

                    return new QryServiceInfoModel(
                            qryServiceInfoListPo.getAppIp(), qryServiceInfoListPo.getTsiServiceBeanName(), qryServiceInfoListPo.getTsiServiceName(),
                            qryServiceInfoListPo.getTsiServicePath(), qryServiceInfoListPo.getTsiProvider(),
                            consumers, providerNames, consumerNames, qryServiceInfoListPo.getGmtModify()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * List<QryAppHttpReqListPO> -> List<QryHttpInfoModel>
     */
    public static List<QryHttpInfoModel> createQryHttpInfoModel(List<QryAppHttpReqListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryAppHttpReqListPo -> new QryHttpInfoModel(
                        qryAppHttpReqListPo.getThfReqIp(), qryAppHttpReqListPo.getThfUrl(), qryAppHttpReqListPo.getThfTakeupTime(),
                        qryAppHttpReqListPo.getThfReqTimestamp(), qryAppHttpReqListPo.getThfResultSucess(), qryAppHttpReqListPo.getThfInput(),
                        qryAppHttpReqListPo.getThfResult(), qryAppHttpReqListPo.getGmtModify()
                ))
                .collect(Collectors.toList());
    }

    /**
     * List<QryMqTraceInfoListPO> -> List<QryMqTraceInfoModel>
     */
    public static List<QryMqTraceInfoModel> createQryMqTraceInfoModel(List<QryMqTraceInfoListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryMqTraceInfoListPo -> new QryMqTraceInfoModel(
                        qryMqTraceInfoListPo.getTmfTopic(), qryMqTraceInfoListPo.getTmfReqIp(), qryMqTraceInfoListPo.getTmfTag(),
                        qryMqTraceInfoListPo.getTmfMsgId(), new String(qryMqTraceInfoListPo.getTmfMqContent(), StandardCharsets.UTF_8), qryMqTraceInfoListPo.getTmfRespIp(),
                        qryMqTraceInfoListPo.getTmfConsumerStartTimestamp(), qryMqTraceInfoListPo.getTmfConsumerStartTimestamp(),
                        qryMqTraceInfoListPo.getTmfConsumerTakeupTimestamp(), qryMqTraceInfoListPo.getGmtModify()
                ))
                .collect(Collectors.toList());
    }

    /**
     * List<QryAppThreadInfoListPO> -> List<QryThreadsInfoModel>
     */
    public static List<QryThreadsInfoModel> createQryThreadsInfoModel(List<QryAppThreadInfoListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryAppThreadInfoListPo -> new QryThreadsInfoModel(
                        qryAppThreadInfoListPo.getTatiAppIp(), qryAppThreadInfoListPo.getTatiThreadId(), qryAppThreadInfoListPo.getTatiThreadName(),
                        qryAppThreadInfoListPo.getTatiThreadStatus(), qryAppThreadInfoListPo.getTatiThreadStartMtime(), qryAppThreadInfoListPo.getTatiThreadBlockedCount(),
                        qryAppThreadInfoListPo.getTatiThreadBlockedCount(), qryAppThreadInfoListPo.getTatiThreadBlockedName(), qryAppThreadInfoListPo.getTatiThreadBlockedId(),
                        qryAppThreadInfoListPo.getTatiThreadWaitedCount(), qryAppThreadInfoListPo.getTatiThreadWaitedMtime(), qryAppThreadInfoListPo.getTatiThreadMaxCount(),
                        qryAppThreadInfoListPo.getTatiThreadStack(), qryAppThreadInfoListPo.getGmtModify()
                ))
                .collect(Collectors.toList());
    }

    /**
     * QryRedisInfoOutPO -> QryRedisInfoModel
     */
    public static QryRedisInfoModel createQryRedisInfoModel(QryRedisInfoOutPO pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return new QryRedisInfoModel(
                pos.getTrhAppIp(), pos.getTrhRedisIp(), pos.getTrhRedisFlag(), pos.getTrhSlaveIp(),
                pos.getTrhClusterType(), pos.getTrhRdbOpen(), pos.getTrhRdbFile(),
                pos.getTrhRdbSaveType(), pos.getTrhAofOpen(), pos.getTrhAofFile(),
                pos.getTrhAofSaveType(), pos.getTrhAofRdbOpen(), pos.getTrhConnBlockCount(),
                pos.getTrhMemoryAvailableCount(), pos.getTrhMemoryPeak(), pos.getTrhKeyspaceRatio(),
                pos.getTrhKeyspaceRatio(), pos.getTrhKeysCount(), pos.getTrhLastRdbStatus(),
                pos.getTrhLastAofStatus(), pos.getTrhLastForkUsec(), pos.getTrhConnTotalCount(),
                pos.getTrhConnCurCount(), pos.getTrhConnBlockCount(), pos.getTrhLogPath(),
                pos.getTrhConfigPath(), pos.getTrhSentinelMonitor(), pos.getTrhSentinelConfigPath()
        );
    }

    /**
     * QryAppSlowSqlListPO -> QrySlowSqlInfoModel
     */
    public static List<QrySlowSqlInfoModel> createQrySlowSqlInfoModel(List<QryAppSlowSqlListPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(qryAppSlowSqlListPo -> new QrySlowSqlInfoModel(
                        qryAppSlowSqlListPo.getTssReqIp(), qryAppSlowSqlListPo.getTssSlowSql(), qryAppSlowSqlListPo.getTssTakeTime(),
                        qryAppSlowSqlListPo.getTssExplain(), qryAppSlowSqlListPo.getTssExplainContent(), qryAppSlowSqlListPo.getGmtModify()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 创建实例 : QryAppMermoryInfoOutDTO
     */
    public static QryAppMermoryInfoOutDTO createQryAppMermoryInfoOutDTO(List<QryAppMermoryInfoModel> models) {
        QryAppMermoryInfoOutDTO outDto = new QryAppMermoryInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            outDto.setList(models);
        }

        return outDto;
    }

    /**
     * 创建实例 : QrySlowSqlInfoOutDTO
     */
    public static QrySlowSqlInfoOutDTO createQrySlowSqlInfoOutDTO(List<QrySlowSqlInfoModel> models) {
        QrySlowSqlInfoOutDTO outDto = new QrySlowSqlInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            outDto.setQrySlowSqlInfoList(models);
        }

        return outDto;
    }

    /**
     * 创建实例 : QryHttpInfoOutDTO
     */
    public static QryHttpInfoOutDTO createQryHttpInfoOutDTO(List<QryHttpInfoModel> models) {
        QryHttpInfoOutDTO qryHttpInfoOutDTO = new QryHttpInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            qryHttpInfoOutDTO.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            qryHttpInfoOutDTO.setList(models);
        }

        return qryHttpInfoOutDTO;
    }

    /**
     * 创建实例 : QryMqTraceInfoOutDTO
     */
    public static QryMqTraceInfoOutDTO createQryMqTraceInfoOutDTO(List<QryMqTraceInfoModel> models) {
        QryMqTraceInfoOutDTO outDto = new QryMqTraceInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            outDto.setList(models);
        }

        return outDto;
    }

    /**
     * 创建实例 : QryRedisInfoOutDTO
     */
    public static QryRedisInfoOutDTO createQryRedisInfoOutDTO(List<QryRedisInfoModel> models) {
        QryRedisInfoOutDTO outDto = new QryRedisInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            outDto.setList(models);
        }

        return outDto;
    }

    /**
     * 创建实例 : QryServiceInfoOutDTO
     */
    public static QryServiceInfoOutDTO createQryServiceInfoOutDTO(List<QryServiceInfoModel> models) {
        QryServiceInfoOutDTO outDto = new QryServiceInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
        } else {
            outDto.setQryServiceInfos(models);
        }

        return outDto;
    }

    /**
     * 创建实例 : QryThreadsInfoOutDTO
     */
    public static QryThreadsInfoOutDTO createQryThreadsInfoOutDTO(List<QryThreadsInfoModel> models, int total) {
        QryThreadsInfoOutDTO outDto = new QryThreadsInfoOutDTO();
        if (CollectionUtils.isEmpty(models)) {
            outDto.setErrorCode(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND);
            outDto.setErrorMsg(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND_MESSAGE);
        } else {
            outDto.setList(models);
            outDto.setTotal(total);
        }

        return outDto;
    }

    /**
     * 创建实例 : QryAppHttpReqPO
     */
    public static QryAppHttpReqPO createQryAppHttpReqPO(String targetIp, QryHttpReqInfoListDO qryHttpReqInfoListDo) {
        int limit = qryHttpReqInfoListDo.getLimit();
        limit = Math.max(limit, 7);
        return new QryAppHttpReqPO(targetIp, limit);
    }

    /**
     * 创建实例 : QryMqTraceInfoPO
     */
    public static QryMqTraceInfoPO createQryMqTraceInfoPO(String appIp, QryMqTraceInfoListDO qryMqTraceInfoListDo) {
        int limit = qryMqTraceInfoListDo.getLimit();
        limit = Math.max(limit, 7);
        return new QryMqTraceInfoPO(appIp, qryMqTraceInfoListDo.getTopic(), qryMqTraceInfoListDo.getMsgId(), limit);
    }

    /**
     * 创建实例 : QryRedisInfoPO
     */
    public static QryRedisInfoPO createQryRedisInfoPO(String appIp) {
        return new QryRedisInfoPO(appIp);
    }

    /**
     * 创建实例 : QryServiceInfoPO
     */
    public static QryServiceInfoPO createQryServiceInfoPO(String appIp, QryAppServiceInfoListDO qryAppServiceInfoListDo) {
        return new QryServiceInfoPO(appIp, qryAppServiceInfoListDo.getServiceName());
    }

    /**
     * 创建实例 : QryAppSlowSqlPO
     */
    public static QryAppSlowSqlPO createQryAppSlowSqlListPO(String appIp, QryAppSlowSqlListDO qryAppSlowSqlListDo) {
        int limit = qryAppSlowSqlListDo.getLimit();
        limit = Math.max(limit, 7);
        return new QryAppSlowSqlPO(appIp, limit);
    }

    /**
     * 创建实例 : QryAppThreadInfoPO
     */
    public static QryAppThreadInfoPO createQryAppThreadInfoPO(String appIp, QryAppThreadInfoListDO qryAppThreadInfoListDo) {
        return new QryAppThreadInfoPO(appIp, qryAppThreadInfoListDo.getThreadName());
    }
}
