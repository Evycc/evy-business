package com.evy.common.trace.service;

import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.*;
import com.evy.common.trace.infrastructure.tunnel.po.TraceMemoryInfoPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceRedisPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceServiceUpdatePO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceThreadInfoPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.JsonUtils;
import com.evy.common.web.utils.UdpUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 收集服务器健康信息
 * @Author: EvyLiuu
 * @Date: 2021/4/29 0:49
 */
public class HealthyInfoService {
    private static int PORT = 5464;
    private static final String SWITCH1 = "TraceDBModel";
    private static final String SWITCH2 = "TraceMemoryInfoPO";
    private static final String SWITCH3 = "TraceHttpModel";
    private static final String SWITCH4 = "TraceMqModel";
    private static final String SWITCH5 = "TraceThreadInfoPO";
    private static final String SWITCH6 = "TraceRedisPO";
    private static final String SWITCH7 = "TraceServiceModel";
    private static final String SWITCH8 = "TraceServiceUpdatePO";

    static {
        AppContextUtils.getSyncProp(businessProperties -> {
            if (Objects.nonNull(businessProperties)) {
                PORT = businessProperties.getTrace().getHealthy().getPort();
            }
            UdpUtils.serverAsync(PORT, request -> {
                String json = new String(request, StandardCharsets.UTF_8);
                try {
                    HealthyInfoModel healthyInfoModel = JsonUtils.convertToObject(json, HealthyInfoModel.class);
                    String code = healthyInfoModel.getClassCodeName();
                    switch (code) {
                        case SWITCH1:break;
                        case SWITCH2:break;
                        case SWITCH3:break;
                        case SWITCH4:break;
                        case SWITCH5:break;
                        case SWITCH6:break;
                        case SWITCH7:break;
                        case SWITCH8:break;
                        default:break;
                    }

                } catch (Exception e) {
                    CommandLog.error("接收到异常健康信息:{}", json);
                    CommandLog.errorThrow("健康信息收集异常", e);
                }
            });
        });

    }
}
