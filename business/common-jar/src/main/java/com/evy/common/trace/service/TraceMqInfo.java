package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.domain.factory.MqFactory;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.TraceMqModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceMqListPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceMqPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.web.utils.UdpUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * 链路跟踪<br/>
 * MQ : 统计topic请求数、msg耗时、msgID、msgId请求时间 | 配置 : evy.trace.mq.flag={0开启|1关闭}<br/>
 * @Author: EvyLiuu
 * @Date: 2020/6/27 16:15
 */
public class TraceMqInfo {
    /**
     * 配置常量
     **/
    private static boolean MQ_PRPO = false;
    private static final ConcurrentLinkedQueue<TraceMqModel> MQ_MODELS = new ConcurrentLinkedQueue<>();
    /**
     * SQL常量
     **/
    private static final String MQ_SEND_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.mqSendInsert";
    private static final String MQ_CONSUMER_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.mqConsumerInsert";
    private static final String MQ_SEND_LIST_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.mqSendListInsert";
    private static final String MQ_CONSUMER_LIST_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.mqConsumerListInsert";

    static {
        AppContextUtils.getAsyncProp(businessProperties -> {
            MQ_PRPO = businessProperties.getTrace().getMq().isFlag();
            CommandLog.info("开启记录MQ链路: {}", MQ_PRPO);
        });
    }

    /**
     * 记录mq生产者信息
     *
     * @param mqSendMessage com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage mq消息体
     */
    public static void addTraceMqSend(MqSendMessage mqSendMessage) {
        try {
            if (MQ_PRPO) {
                String topic = mqSendMessage.getTopic();
                String tag = mqSendMessage.getTag();
                if (mqSendMessage.getTopic().equals(MqFactory.DLX_EXCHANGE)) {
                    topic = mqSendMessage.getPrpoMap().getOrDefault(MqFactory.X_DEAD_LETTER_EXCHANGE, BusinessConstant.EMPTY_STR);
                    tag = mqSendMessage.getPrpoMap().getOrDefault(MqFactory.X_DEAD_LETTER_ROUTING_KEY, BusinessConstant.EMPTY_STR);
                }
                MQ_MODELS.offer(TraceMqModel.create(StringUtils.isEmpty(mqSendMessage.getPrpoMap().get(MqFactory.SEND_HOST))
                                ? BusinessConstant.VM_HOST : mqSendMessage.getPrpoMap().get(MqFactory.SEND_HOST),
                        topic, tag, mqSendMessage.getMessageId(), String.valueOf(mqSendMessage.getMessage())));

            }
        } catch (Exception e) {
            CommandLog.errorThrow("addTraceHttp Error!", e);
        }
    }

    /**
     * 记录mq消费者信息
     *
     * @param msgId           msgID
     * @param takeUpTimestamp 消费结束时间
     */
    public static void addTraceMqEnd(String msgId, long takeUpTimestamp) {
        try {
            if (MQ_PRPO) {
                MQ_MODELS.offer(TraceMqModel.create(BusinessConstant.VM_HOST, takeUpTimestamp, msgId));
            }
        } catch (Exception e) {
            CommandLog.errorThrow("addTraceHttp Error!", e);
        }
    }

    /**
     * 处理MQ类型逻辑,入库并清理内存中链表对象
     */
    public static void executeMq() {
        int size = MQ_MODELS.size();
        try {
            if (size > BusinessConstant.ZERO_NUM) {
                List<TraceMqModel> mqModels = new ArrayList<>(MQ_MODELS);
                if (HealthyInfoService.isIsHealthyService()) {
                    UdpUtils.send(HealthyInfoService.getHostName(), HealthyInfoService.getPort(), HealthyInfoModel.create(TraceMqModel.class, mqModels));
                } else {
                    addMqTraceInfo(mqModels);
                }

                MQ_MODELS.removeAll(mqModels);
            }
        } catch (Exception e) {
            //捕捉记录trace的异常，不影响业务功能
            CommandLog.warn("记录executeMq异常", e);
        }
    }

    /**
     * 更新MQ发布消息信息
     * @param traceMqModels com.evy.common.trace.infrastructure.tunnel.model.TraceMqModel
     */
    public static void addMqTraceInfo(List<TraceMqModel> traceMqModels) {
        int size = traceMqModels.size();
        try {
            if (size > BusinessConstant.ZERO_NUM) {

                List<TraceMqPO> sendList = traceMqModels.stream()
                        .filter(traceMqModel -> traceMqModel.getTopic() != null)
                        .map(TraceMqInfo::buildTraceMqSendPo)
                        .collect(Collectors.toList());

                if (sendList.size() > 0) {
                    if (sendList.size() == 1) {
                        DBUtils.insert(MQ_SEND_INSERT, sendList.get(0));
                    } else {
                        DBUtils.insert(MQ_SEND_LIST_INSERT, TraceMqListPO.create(sendList));
                    }
                }


                List<TraceMqPO> consumerList = traceMqModels.stream()
                        .filter(traceMqModel -> traceMqModel.getTopic() == null)
                        .map(TraceMqInfo::buildTraceMqConsumerPo)
                        .collect(Collectors.toList());

                if (consumerList.size() > 0) {
                    if (consumerList.size() == 1) {
                        DBUtils.insert(MQ_CONSUMER_INSERT, consumerList.get(0));
                    } else {
                        DBUtils.insert(MQ_CONSUMER_LIST_INSERT, TraceMqListPO.create(consumerList));
                    }
                }

                traceMqModels = null;
                sendList = null;
                consumerList = null;
            }
        } catch (Exception e) {
            //捕捉记录trace的异常，不影响业务功能
            CommandLog.warn("记录addMqTraceInfo异常", e);
        }
    }

    /**
     * 构建MQ生产者信息PO,用于记录trace表
     *
     * @param traceMqModel com.evy.common.trace.tunnel.model.TraceMqModel
     * @return com.evy.common.trace.tunnel.po.TraceMqPO
     */
    private static TraceMqPO buildTraceMqSendPo(TraceMqModel traceMqModel) {
        return TraceMqPO.createSend(traceMqModel.getTopic(), traceMqModel.getReqIp(),
                traceMqModel.getTag(), traceMqModel.getMsgId(), traceMqModel.getMqContent(), traceMqModel.getReqTimestamp());
    }

    /**
     * 构建MQ消费者信息PO,用于记录trace表
     *
     * @param traceMqModel com.evy.common.trace.tunnel.model.TraceMqModel
     * @return com.evy.common.trace.tunnel.po.TraceMqPO
     */
    private static TraceMqPO buildTraceMqConsumerPo(TraceMqModel traceMqModel) {
        return TraceMqPO.createConsume(traceMqModel.getMsgId(), traceMqModel.getReqIp(),
                traceMqModel.getReqTimestamp(), String.valueOf(traceMqModel.getTakeUpTimestamp()));
    }
}
