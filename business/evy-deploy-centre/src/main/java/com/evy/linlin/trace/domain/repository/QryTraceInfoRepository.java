package com.evy.linlin.trace.domain.repository;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.HeapDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.ThreadDumpInfoModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO;
import com.evy.common.trace.service.TraceJvmManagerUtils;
import com.evy.common.trace.service.TraceThreadInfo;
import com.evy.common.trace.service.TraceTracking;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.JsonUtils;
import com.evy.common.web.HealthyControllerConstant;
import com.evy.common.web.tunnel.dto.HttpRequestDTO;
import com.evy.common.web.utils.HttpUtils;
import com.evy.linlin.trace.domain.tunnel.QryTraceAssembler;
import com.evy.linlin.trace.domain.tunnel.constant.QryTraceErrorConstant;
import com.evy.linlin.trace.domain.tunnel.model.*;
import com.evy.linlin.trace.domain.tunnel.po.*;
import com.evy.linlin.trace.dto.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取监控信息(内存、redis、线程...)
 *
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:11
 */
@Repository
public class QryTraceInfoRepository {
    private final HttpUtils httpUtils;
    private final QryTraceInfoDataRepository dataRepository;

    public QryTraceInfoRepository(QryTraceInfoDataRepository dataRepository, HttpUtils httpUtils) {
        this.dataRepository = dataRepository;
        this.httpUtils = httpUtils;
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
    public Map<String, List<QryAppMermoryInfoModel>> qryAppMermoryInfoList(QryAppMermoryInfoListDO qryAppMermoryListDo) {
        List<QryAppMermoryInfoModel> result = new ArrayList<>();
        Map<String, List<QryAppMermoryInfoModel>> resultMap = new HashMap<>(8);
        List<String> targetIpList = getTargetIp(qryAppMermoryListDo.getSeq(), qryAppMermoryListDo.getUserSeq());

        if (!CollectionUtils.isEmpty(targetIpList)) {
            targetIpList.stream()
                    .map(targetIp -> dataRepository.qryAppMermoryList(
                            QryTraceAssembler.createQryAppMermoryPO(targetIp))
                    )
                    .map(QryTraceAssembler::createQryAppMermoryInfoModel)
                    .filter(models -> !CollectionUtils.isEmpty(models))
                    .reduce((list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    })
                    .ifPresent(result::addAll);
        }

        //归类
        targetIpList.forEach(targetIp -> {
            List<QryAppMermoryInfoModel> temp = result.stream()
                    .filter(qryAppMermoryInfoModel -> qryAppMermoryInfoModel.getAppIp().equals(targetIp))
                    .collect(Collectors.toList());
            resultMap.put(targetIp, temp);
        });

        return resultMap;
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
            List<QryRedisInfoOutPO> redisInfoOutPos = dataRepository.qryRedisInfo(QryTraceAssembler.createQryRedisInfoPO(targetIpList));
            result = QryTraceAssembler.createQryRedisInfoModel(redisInfoOutPos);
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
     *
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
        if (CollectionUtils.isEmpty(result)) {
            List<TraceThreadInfoPO> list = TraceThreadInfo.findAllThreads();
            List<QryAppThreadInfoListPO> list1 =
                    list.stream()
                            .map(traceThreadInfoPo -> new QryAppThreadInfoListPO(
                                    traceThreadInfoPo.getTatiAppIp(), traceThreadInfoPo.getTatiThreadAvailByte(), traceThreadInfoPo.getTatiThreadName(),
                                    traceThreadInfoPo.getTatiThreadStatus(), traceThreadInfoPo.getTatiThreadStartMtime(), traceThreadInfoPo.getTatiThreadBlockedMtime(),
                                    traceThreadInfoPo.getTatiThreadBlockedMtime(), traceThreadInfoPo.getTatiThreadBlockedName(), traceThreadInfoPo.getTatiThreadBlockedMtime(),
                                    traceThreadInfoPo.getTatiThreadWaitedMtime(), traceThreadInfoPo.getTatiThreadWaitedMtime(), traceThreadInfoPo.getTatiThreadAvailByte(),
                                    traceThreadInfoPo.getTatiThreadStack()
                            )).collect(Collectors.toList());
            result = QryTraceAssembler.createQryThreadsInfoModel(list1);
            list = null;
            list1 = null;
        }

        return result;
    }

    /**
     * 新增服务码
     *
     * @param infoDo com.evy.linlin.trace.domain.tunnel.model.CreateNewSrvInfoDo
     * @return true: 新增成功
     */
    public boolean createNewSrvInfo(CreateNewSrvInfoDo infoDo) throws BasicException {
        boolean result;
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
     *
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

    /**
     * 查询traceId调用链路，按时间升序返回
     *
     * @param infoDO traceId
     * @return 调用信息模型
     */
    public QryTrackingInfoOutDO qryTrackingInfo(QryTrackingInfoDO infoDO) {
        QryTrackingInfoOutDO outDo = null;
        List<String> list = TraceTracking.searchTraceList(infoDO.getTraceId());
        if (!CollectionUtils.isEmpty(list)) {
            //解析traceId
            //MQ traceId|链路类型|应用名|耗时|时间戳|{0发布者|1消费者}|topic|tag
            //DB traceId|链路类型|应用名|耗时|时间戳|数据库名
            //SERVICE traceId|链路类型|应用名|耗时|时间戳|服务码
            //HTTP traceId|链路类型|应用名|耗时|时间戳|http请求路径
            List<QryTrackingInfoModel> models = list.stream()
                    .map(str -> str.split(BusinessConstant.SPLIT_LINE, -1))
                    .map(strings -> {
                        String traceId = strings[0];
                        String reqType = strings[1];
                        String appName = strings[2];
                        String takeTimeMs = strings[3];
                        long order = Long.parseLong(strings[4]);
                        String remakes = null;
                        boolean isProvider = false;
                        if (BusinessConstant.ZERO.equals(reqType) || BusinessConstant.ONE.equals(reqType) || "2".equals(reqType)) {
                            remakes = strings[5];
                        } else if ("3".equals(reqType)) {
                            isProvider = strings[5].equals(BusinessConstant.ZERO);
                            remakes = "[topic] " + strings[6] + "\n" + "[tag] " + strings[7];
                        }

                        return new QryTrackingInfoModel(traceId, reqType, takeTimeMs, remakes, order, isProvider, appName);
                    })
                    .sorted((m1, m2) -> (int) (m1.getOrder() - m2.getOrder()))
                    .collect(Collectors.toList());
            outDo = new QryTrackingInfoOutDO(models);
        }

        return outDo;
    }

    public SearchDumpOutDO searchDump(SearchDumpDO searchDumpDo) {
        SearchDumpOutDO result = new SearchDumpOutDO();
        boolean isLocalReq = BusinessConstant.VM_HOST.equals(searchDumpDo.getTargetIp());
        switch (searchDumpDo.getCode()) {
            case HealthyControllerConstant.DEAD_THREAD_DUMP_CODE:
                result.setDeadThreadList(findDeadThreads(searchDumpDo.getTargetIp(), isLocalReq));
                break;
            case HealthyControllerConstant.HEAP_DUMP_CODE:
                result.setHeapDumpInfo(heapDump(searchDumpDo.getTargetIp(), isLocalReq));
                break;
            case HealthyControllerConstant.THREAD_DUMP_CODE:
                result.setThreadInfo(threadDump(searchDumpDo.getTargetIp(), searchDumpDo.getThreadId(), isLocalReq));
                break;
            default:
                break;
        }
        return result;
    }

    private HeapDumpInfoModel heapDump(String targetIp, boolean isLocalReq) {
        HeapDumpInfoModel model = null;
        if (isLocalReq) {
            model = TraceJvmManagerUtils.heapDump();
        }
        try {
            String url = "http://" + targetIp + ":" + AppContextUtils.getForEnv("server.port")
                    + "/" + HealthyControllerConstant.HEALTHY_CONTROLLER_CODE
                    + "/" + HealthyControllerConstant.HEAP_DUMP_CODE;
            model = httpUtils.httpRequest(HttpRequestDTO.create(url, new HttpGet(), null, null, null),
                    httpResponse -> {
                        HeapDumpInfoModel response = new HeapDumpInfoModel();
                        try {
                            response = JsonUtils.convertToObject(EntityUtils.toString(httpResponse.getEntity()), HeapDumpInfoModel.class);
                        } catch (IOException e) {
                            CommandLog.errorThrow("heapDump 返回异常", e);
                        }

                        return response;
                    });
        } catch (URISyntaxException e) {
            CommandLog.errorThrow("heapDump URI构建异常", e);
        } catch (IOException ioe) {
            CommandLog.errorThrow("heapDump 请求异常", ioe);
        }
        return model;
    }

    private List<ThreadDumpInfoModel> findDeadThreads(String targetIp, boolean isLocalReq) {
        List<ThreadDumpInfoModel> models = null;
        if (isLocalReq) {
            models = TraceJvmManagerUtils.findDeadThreads();
        }
        try {
            String url = "http://" + targetIp + ":" + AppContextUtils.getForEnv("server.port")
                    + "/" + HealthyControllerConstant.HEALTHY_CONTROLLER_CODE
                    + "/" + HealthyControllerConstant.DEAD_THREAD_DUMP_CODE;
            models = httpUtils.httpRequest(HttpRequestDTO.create(url, new HttpGet(), null, null, null),
                    httpResponse -> {
                        List<ThreadDumpInfoModel> response = new ArrayList<>();
                        try {
                            response = JsonUtils.convertToObject(EntityUtils.toString(httpResponse.getEntity()), new TypeToken<List<ThreadDumpInfoModel>>() {
                            }.getType());
                        } catch (IOException e) {
                            CommandLog.errorThrow("threadDump 返回异常", e);
                        }

                        return response;
                    });
        } catch (URISyntaxException e) {
            CommandLog.errorThrow("findDeadThreads URI构建异常", e);
        } catch (IOException ioe) {
            CommandLog.errorThrow("findDeadThreads 请求异常", ioe);
        }
        return models;
    }

    private ThreadDumpInfoModel threadDump(String targetIp, long threadId, boolean isLocalReq) {
        ThreadDumpInfoModel model = null;
        if (isLocalReq) {
            model = TraceJvmManagerUtils.threadDump(threadId);
        }
        try {
            String url = "http://" + targetIp + ":" + AppContextUtils.getForEnv("server.port")
                    + "/" + HealthyControllerConstant.HEALTHY_CONTROLLER_CODE
                    + "/" + HealthyControllerConstant.THREAD_DUMP_CODE;
            model = httpUtils.httpRequest(HttpRequestDTO.create(url, new HttpPost(), new StringEntity(String.valueOf(threadId), ContentType.APPLICATION_JSON), null, null),
                    httpResponse -> {
                        ThreadDumpInfoModel response = new ThreadDumpInfoModel();
                        try {
                            response = JsonUtils.convertToObject(EntityUtils.toString(httpResponse.getEntity()), ThreadDumpInfoModel.class);
                        } catch (IOException e) {
                            CommandLog.errorThrow("threadDump 返回异常", e);
                        }

                        return response;
                    });
        } catch (URISyntaxException e) {
            CommandLog.errorThrow("threadDump URI构建异常", e);
        } catch (IOException ioe) {
            CommandLog.errorThrow("threadDump 请求异常", ioe);
        }
        return model;
    }
}
