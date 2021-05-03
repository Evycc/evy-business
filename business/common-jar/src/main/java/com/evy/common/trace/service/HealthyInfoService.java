package com.evy.common.trace.service;

import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.HealthyInfoModel;
import com.evy.common.trace.infrastructure.tunnel.model.TraceHttpModel;
import com.evy.common.trace.infrastructure.tunnel.model.TraceMqModel;
import com.evy.common.trace.infrastructure.tunnel.model.TraceServiceModel;
import com.evy.common.trace.infrastructure.tunnel.po.*;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.JsonUtils;
import com.evy.common.web.utils.UdpUtils;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 收集服务器健康信息
 * @Author: EvyLiuu
 * @Date: 2021/4/29 0:49
 */
public class HealthyInfoService {
    private static final String END_INDEX = "}";
    private static int PORT = 5464;
    private static String HOSTNAME = "";
    /**
     * 判断是否需要将健康信息发送到监控服务器
     */
    private static boolean IS_HEALTHY_SERVICE = false;
    private static final String SWITCH1 = "TraceSqlPO";
    private static final String SWITCH2 = "TraceMemoryInfoPO";
    private static final String SWITCH3 = "TraceHttpModel";
    private static final String SWITCH4 = "TraceMqModel";
    private static final String SWITCH5 = "TraceThreadInfoPO";
    private static final String SWITCH6 = "TraceRedisPO";
    private static final String SWITCH7 = "TraceServiceModel";
    private static final String SWITCH8 = "TraceServiceUpdatePO";

    static {
        AppContextUtils.getAsyncProp(businessProperties -> {
            if (Objects.nonNull(businessProperties)) {
                IS_HEALTHY_SERVICE = !StringUtils.isEmpty(businessProperties.getTrace().getHealthy().getSendHost());
                PORT = businessProperties.getTrace().getHealthy().getPort();
                HOSTNAME = businessProperties.getTrace().getHealthy().getSendHost();

                if (businessProperties.getTrace().getHealthy().isService()) {
                    openHealthyServer();
                }
            }
        });
    }

    /**
     * 开启监控端口
     */
    public static void openHealthyServer() {
        UdpUtils.serverAsync(PORT, request -> {
            int endIndex = request.length;
            for (int i = 0; i < request.length; i++) {
                if (request[i] == 0) {
                    endIndex = i;
                    break;
                }
            }
            byte[] bytes = new byte[endIndex];
            System.arraycopy(request, 0, bytes, 0, endIndex);
            String json = new String(bytes, StandardCharsets.UTF_8);
            try {
                HealthyInfoModel healthyInfoModel = JsonUtils.convertToObject(json, HealthyInfoModel.class);
                String code = healthyInfoModel.getClassCodeName();
                String traceInfoJson = healthyInfoModel.getTraceInfoJson();
                CommandLog.info("Receive Code:{}", code);
                switch (code) {
                    case SWITCH1:
                        //更新慢sql信息
                        TraceSqlPO traceSqlPo = JsonUtils.convertToObject(traceInfoJson, TraceSqlPO.class);
                        TraceSlowSql.addSlowSql(traceSqlPo);
                        break;
                    case SWITCH2:
                        //更新服务器内存信息
                        TraceMemoryInfoPO traceMemoryInfoPo = JsonUtils.convertToObject(traceInfoJson, TraceMemoryInfoPO.class);
                        TraceAppMemoryInfo.addMemoryInfo(traceMemoryInfoPo);
                        break;
                    case SWITCH3:
                        //更新http请求信息
                        List<TraceHttpModel> traceHttpModels = JsonUtils.convertToObject(traceInfoJson, new TypeToken<List<TraceHttpModel>>(){}.getType());
                        TraceHttpInfo.addHttpTraceInfo(traceHttpModels);
                        break;
                    case SWITCH4:
                        //更新MQ发布消息信息
                        List<TraceMqModel> traceMqModels = JsonUtils.convertToObject(traceInfoJson, new TypeToken<List<TraceMqModel>>(){}.getType());
                        TraceMqInfo.addMqTraceInfo(traceMqModels);
                        break;
                    case SWITCH5:
                        //更新服务器线程信息
                        List<TraceThreadInfoPO> traceThreadInfos = JsonUtils.convertToObject(traceInfoJson, new TypeToken<List<TraceThreadInfoPO>>(){}.getType());
                        TraceThreadInfo.addThreadInfos(traceThreadInfos);
                        break;
                    case SWITCH6:
                        //更新redis服务器监控信息
                        TraceRedisPO traceRedisPo = JsonUtils.convertToObject(traceInfoJson, TraceRedisPO.class);
                        TraceRedisInfo.addRedisInfo(traceRedisPo);
                        break;
                    case SWITCH7:
                        //更新应用上线信息
                        List<TraceServiceModel> traceServiceModels = JsonUtils.convertToObject(traceInfoJson, new TypeToken<List<TraceServiceModel>>(){}.getType());
                        TraceService.addServiceInfo(traceServiceModels, healthyInfoModel.getAppName());
                        break;
                    case SWITCH8:
                        //更新应用下线信息
                        TraceServiceUpdatePO updatePo = JsonUtils.convertToObject(traceInfoJson, TraceServiceUpdatePO.class);
                        TraceService.cleanServiceInfo(updatePo);
                        break;
                    default:break;
                }

            } catch (Exception e) {
                CommandLog.error("接收到异常健康信息:{}", json);
                CommandLog.errorThrow("健康信息收集异常", e);
            }
        });
    }

    public static int getPort() {
        return PORT;
    }

    public static String getHostName() {
        return HOSTNAME;
    }

    public static boolean isIsHealthyService() {
        return IS_HEALTHY_SERVICE;
    }
}
