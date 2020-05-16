package com.evy.common.app.event.log;

import com.evy.common.domain.repository.db.DBUtils;
import com.evy.common.domain.repository.mq.basic.BaseRabbitMqConsumer;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.command.utils.JsonUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 记录交易流水日志到数据表public_log_flow<br/>
 * @Author: EvyLiuu
 * @Date: 2019/11/24 20:07
 */
@Component
public class TraceLogEvent extends BaseRabbitMqConsumer {
    private final static String INSERT_LOG = "INSERT INTO public_log_flow(plf_srcSendNo,plf_code,plf_clientIp,plf_serverIp,plf_reqContent,plf_errorCode,plf_errorMsg) VALUES (?,?,?,?,?,?,?)";
    private static final String SRCSENDNO = "srcSendNo";
    private static final String CODE = "code";
    private static final String CLIENTIP = "clientIp";
    private static final String SERVERIP = "serverIp";
    private static final String REQCONTENT = "reqContent";
    private static final String ERRORCODE = "errorCode";
    private static final String ERRORMSG = "errorMsg";

    public TraceLogEvent(Channel channel) {
        super(channel);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
        assert sendMessage != null;
        String messageJson = String.valueOf(sendMessage.getMessage());
        Map<String, String> map = JsonUtils.convertToObject(messageJson, Map.class);

        List<Object> params = new ArrayList<>(){{
           add(map.getOrDefault(SRCSENDNO, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(CODE, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(CLIENTIP, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(SERVERIP, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(REQCONTENT, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(ERRORCODE, BusinessConstant.EMPTY_STR));
           add(map.getOrDefault(ERRORMSG, BusinessConstant.EMPTY_STR));
        }};
        int isSuccess = DBUtils.executeUpdateSql(INSERT_LOG, params);

        CommandLog.info("记录到日志流水结果:{}", isSuccess);

        return isSuccess;
    }
}
