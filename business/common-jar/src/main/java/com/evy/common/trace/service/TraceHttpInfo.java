package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.db.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.TraceHttpModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceHttpListPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceHttpPO;
import com.evy.common.utils.AppContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * 链路跟踪<br/>
 * Http :  统计请求总数、耗时、发起方IP、请求地址 | 配置 : evy.trace.http.flag={0开启|1关闭}<br/>
 * @Author: EvyLiuu
 * @Date: 2020/6/27 16:18
 */
public class TraceHttpInfo {
    /**
     * 配置常量
     **/
    private static final String HTTP_PRPO = "evy.trace.http.flag";
    private static final ConcurrentLinkedQueue<TraceHttpModel> HTTP_MODELS = new ConcurrentLinkedQueue<>();
    private static final String HTTP_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.traceHttpInsert";
    private static final String HTTP_LIST_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.traceHttpListInsert";

    /**
     * 记录http请求耗时
     *
     * @param url             http url
     * @param takeUpTimesatmp http响应耗时
     * @param result          http请求结果，true成功，false失败
     * @param inputParam      http请求参数字符串
     * @param respResult      http响应字符串
     */
    public static void addTraceHttp(String url, long takeUpTimesatmp, boolean result, String inputParam, String respResult) {
        try {
            Optional.ofNullable(AppContextUtils.getForEnv(HTTP_PRPO))
                    .ifPresent(flag -> {
                        if (BusinessConstant.ZERO.equals(flag)) {
                            HTTP_MODELS.offer(TraceHttpModel.create(BusinessConstant.VM_HOST, takeUpTimesatmp, url, result, inputParam, respResult));
                        }
                    });
        } catch (Exception e) {
            CommandLog.errorThrow("addTraceHttp Error!", e);
        }
    }

    /**
     * 处理HTTP类型逻辑,入库并清理内存中链表对象
     */
    public static void executeHttp() {
        int size = HTTP_MODELS.size();
        try {
            if (size > BusinessConstant.SUCESS) {
                List<TraceHttpModel> httpModels = new ArrayList<>(HTTP_MODELS);

                List<TraceHttpPO> httpPo = httpModels.stream()
                        .map(TraceHttpInfo::buildTraceHttpPo)
                        .collect(Collectors.toList());

                if (httpPo.size() == BusinessConstant.FAILED) {
                    DBUtils.insert(HTTP_INSERT, httpPo.get(0));
                } else {
                    DBUtils.insert(HTTP_LIST_INSERT, TraceHttpListPO.create(httpPo));
                }

                HTTP_MODELS.removeAll(httpModels);

                httpModels = null;
                httpPo = null;
            }
        } catch (Exception e) {
            //捕捉记录trace的异常，不影响业务功能
            CommandLog.warn("记录executeHttp异常", e);
        }
    }

    /**
     * 构建Http请求信息PO,用于记录trace表
     *
     * @param traceHttpModel com.evy.common.trace.tunnel.model.TraceHttpModel
     * @return com.evy.common.trace.tunnel.po.TraceHttpPO
     */
    private static TraceHttpPO buildTraceHttpPo(TraceHttpModel traceHttpModel) {
        return TraceHttpPO.create(traceHttpModel.getReqIp(), traceHttpModel.getReqUrl(), String.valueOf(traceHttpModel.getTakeUpTimestamp()),
                traceHttpModel.getReqTimestamp(), traceHttpModel.isResultSuccess(), traceHttpModel.getReqParam(), traceHttpModel.getRespResult());
    }
}
