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
    /*---------------- dtoConvertDo DTO转DO ----------------*/

    /**
     * ModifySrvInfoDTO -> ModifySrvInfoDo
     *
     * @param dto com.evy.linlin.trace.dto.ModifySrvInfoDTO
     * @return com.evy.linlin.trace.domain.tunnel.model.ModifySrvInfoDo
     */
    public static ModifySrvInfoDo dtoConvertDo(ModifySrvInfoDTO dto) {
        return new ModifySrvInfoDo(dto.getSrvCode(), dto.getServiceName(), dto.getProviderName(),
                dto.getConsumerName(), dto.getLimitQps(), dto.getLimitFallback());
    }

    /**
     * CreateSrvInfoDTO -> CreateNewSrvInfoDo
     *
     * @param dto com.evy.linlin.trace.dto.CreateSrvInfoDTO
     * @return com.evy.linlin.trace.domain.tunnel.model.CreateNewSrvInfoDo
     */
    public static CreateNewSrvInfoDo dtoConvertDo(CreateSrvInfoDTO dto) {
        return new CreateNewSrvInfoDo(dto.getSrvCode(), dto.getProviderName(), dto.getConsumerName());
    }

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
        return new QryAppSlowSqlListDO(dto.getBuildSeq(), dto.getUserSeq());
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
        return new QryHttpReqInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getPath(), dto.getLimit());
    }

    /**
     * QryTrackingInfoDTO -> QryTrackingInfoDO
     */
    public static QryTrackingInfoDO dtoConvertDo(QryTrackingInfoDTO dto) {
        return new QryTrackingInfoDO(dto.getQryTraceId());
    }

    /**
     * QryMqTraceInfoDTO -> QryMqTraceInfoListDO
     */
    public static QryMqTraceInfoListDO dtoConvertDo(QryMqTraceInfoDTO dto) {
        return new QryMqTraceInfoListDO(dto.getBuildSeq(), dto.getUserSeq(), dto.getTopic(), dto.getMsgId(), dto.getLimit());
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
     * 创建PO SrvInfoPO
     *
     * @param infoDo com.evy.linlin.trace.domain.tunnel.model.CreateNewSrvInfoDo
     * @return com.evy.linlin.trace.domain.tunnel.po.SrvInfoPO
     */
    public static SrvInfoPO doConvertPo(CreateNewSrvInfoDo infoDo) {
        return new SrvInfoPO(infoDo.getSrvCode(), null, infoDo.getProviderName(),
                infoDo.getConsumerName(), -1, null);
    }

    /**
     * 创建PO SrvInfoPO
     *
     * @param infoDo com.evy.linlin.trace.domain.tunnel.model.ModifySrvInfoDo
     * @return com.evy.linlin.trace.domain.tunnel.po.SrvInfoPO
     */
    public static SrvInfoPO doConvertPo(ModifySrvInfoDo infoDo) {
        return new SrvInfoPO(infoDo.getSrvCode(), infoDo.getServiceName(), infoDo.getProviderName(), infoDo.getConsumerName(),
                Objects.isNull(infoDo.getQps()) ? -1 : infoDo.getQps(), infoDo.getFallback());
    }

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
     * 创建实例 : CreateSrvInfoOutDTO
     */
    public static CreateSrvInfoOutDTO createSrvInfoOutDTO() {
        return new CreateSrvInfoOutDTO();
    }

    /**
     * 创建实例 : ModifySrvInfoOutDTO
     */
    public static ModifySrvInfoOutDTO createModifySrvInfoOutDTO() {
        return new ModifySrvInfoOutDTO();
    }

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
                        consumers = Arrays.stream(consumer.split(BusinessConstant.SPLIT_LINE, -1)).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
                    }
                    if (!StringUtils.isEmpty(providerName)) {
                        providerNames = Arrays.stream(providerName.split(BusinessConstant.SPLIT_LINE, -1)).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
                    }
                    if (!StringUtils.isEmpty(consumerName)) {
                        consumerNames = Arrays.stream(consumerName.split(BusinessConstant.SPLIT_LINE, -1)).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
                    }

                    return new QryServiceInfoModel(
                            qryServiceInfoListPo.getTsiServiceBeanName(), qryServiceInfoListPo.getTsiServiceName(),
                            qryServiceInfoListPo.getTsiServicePath(), qryServiceInfoListPo.getTsiProvider(),
                            consumers, providerNames, consumerNames, qryServiceInfoListPo.getGmtModify(), null, BusinessConstant.EMPTY_STR
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
    public static List<QryRedisInfoModel> createQryRedisInfoModel(List<QryRedisInfoOutPO> pos) {
        if (Objects.isNull(pos)) {
            return null;
        }
        return pos.stream()
                .map(po -> new QryRedisInfoModel(
                        po.getTrhAppIp(), po.getTrhRedisIp(), po.getTrhRedisFlag(), po.getTrhSlaveIp(),
                        po.getTrhClusterType(), po.getTrhRdbOpen(), po.getTrhRdbFile(),
                        po.getTrhRdbSaveType(), po.getTrhAofOpen(), po.getTrhAofFile(),
                        po.getTrhAofSaveType(), po.getTrhAofRdbOpen(), po.getTrhMemoryCount(),
                        po.getTrhMemoryAvailableCount(), po.getTrhMemoryPeak(), po.getTrhKeyspaceRatio(),
                        po.getTrhKeyspaceRatio(), po.getTrhKeysCount(), po.getTrhLastRdbStatus(),
                        po.getTrhLastAofStatus(), po.getTrhLastForkUsec(), po.getTrhConnTotalCount(),
                        po.getTrhConnCurCount(), po.getTrhConnBlockCount(), po.getTrhLogPath(),
                        po.getTrhConfigPath(), po.getTrhSentinelMonitor(), po.getTrhSentinelConfigPath()
                ))
                .collect(Collectors.toList());
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
     * 创建实例 : QryTrackingInfoOutDTO
     */
    public static QryTrackingInfoOutDTO createQryTrackingInfoOutDTO(QryTrackingInfoOutDO outDo) {
        QryTrackingInfoOutDTO outDto = new QryTrackingInfoOutDTO();
        if (Objects.nonNull(outDo)) {
            outDto.setTraceList(outDo.getModelList());
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
            outDto.setErrorMsg(QryTraceErrorConstant.QRY_TRACE_NOT_FOUND_MESSAGE);
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
    public static QryMqTraceInfoPO createQryMqTraceInfoPO(List<String> appIp, QryMqTraceInfoListDO qryMqTraceInfoListDo) {
        int limit = 1;
        if (Objects.nonNull(qryMqTraceInfoListDo.getLimit())) {
            limit = qryMqTraceInfoListDo.getLimit();
        }
        return new QryMqTraceInfoPO(appIp, qryMqTraceInfoListDo.getTopic(), qryMqTraceInfoListDo.getMsgId(), limit);
    }

    /**
     * 创建实例 : QryRedisInfoPO
     */
    public static QryRedisInfoPO createQryRedisInfoPO(List<String> appIps) {
        return new QryRedisInfoPO(appIps);
    }

    /**
     * 创建实例 : QryServiceInfoPO
     */
    public static QryServiceInfoPO createQryServiceInfoPO(List<String> appIps, QryAppServiceInfoListDO qryAppServiceInfoListDo) {
        return new QryServiceInfoPO(appIps, qryAppServiceInfoListDo.getServiceName());
    }

    /**
     * 创建实例 : QrySrvLimitInfoPO
     */
    public static QrySrvLimitInfoPO createQrySrvLimitInfoPO(QryServiceInfoModel model) {
        return new QrySrvLimitInfoPO(model.getServiceBeanName(), model.getServiceName());
    }

    /**
     * 创建实例 : QryAppSlowSqlPO
     */
    public static QryAppSlowSqlPO createQryAppSlowSqlListPO(List<String> appIps) {
        return new QryAppSlowSqlPO(appIps);
    }

    /**
     * 创建实例 : QryAppThreadInfoPO
     */
    public static QryAppThreadInfoPO createQryAppThreadInfoPO(String appIp, QryAppThreadInfoListDO qryAppThreadInfoListDo) {
        return new QryAppThreadInfoPO(appIp, qryAppThreadInfoListDo.getThreadName());
    }
}
