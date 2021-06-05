package com.evy.common.mq.rabbitmq.app.event;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.infrastructure.tunnel.model.MqSendMessage;
import com.evy.common.mq.rabbitmq.app.basic.BaseRabbitMqConsumer;
import com.evy.common.utils.JsonUtils;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 记录交易流水日志到数据表public_log_flow<br/>
 * @Author: EvyLiuu
 * @Date: 2019/11/24 20:07
 */
@Component("traceLogEvent")
public class TraceLogEvent extends BaseRabbitMqConsumer {
    private final static String INSERT_LOG = "INSERT INTO public_log_flow(plf_srcSendNo,plf_code,plf_trace_id,plf_clientIp,plf_serverIp,plf_reqContent,plf_errorCode,plf_errorMsg) VALUES (?,?,?,?,?,?,?,?)";
    private static final String SRCSENDNO = "srcSendNo";
    private static final String CODE = "code";
    private static final String CLIENTIP = "clientIp";
    private static final String SERVERIP = "serverIp";
    private static final String REQCONTENT = "reqContent";
    private static final String ERRORCODE = "errorCode";
    private static final String ERRORMSG = "errorMsg";
    private static final String TRACEID = "traceId";

    public TraceLogEvent(Channel channel) {
        super(channel);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int execute(MqSendMessage sendMessage) {
        assert sendMessage != null;
        String messageJson = String.valueOf(sendMessage.getMessage());
        Map<String, String> map = JsonUtils.convertToObject(messageJson, Map.class);

        List<Object> params = new ArrayList<>(){{
           add(map.getOrDefault(SRCSENDNO, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(CODE, BusinessConstant.EMPTY_STR));
            add(map.getOrDefault(TRACEID, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(CLIENTIP, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(SERVERIP, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(REQCONTENT, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(ERRORCODE, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(ERRORMSG, BusinessConstant.EMPTY_STR));
        }};
        int isSuccess = DBUtils.executeUpdateSql(INSERT_LOG, params);

        CommandLog.info("记录到日志流水结果:{}", isSuccess);

        return BusinessConstant.SUCESS;
    }
}
