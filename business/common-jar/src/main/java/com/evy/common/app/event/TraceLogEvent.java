package com.evy.common.app.event;

import com.evy.common.domain.repository.db.DBUtils;
import com.evy.common.domain.repository.mq.basic.BasicRabbitMqConsumer;
import com.evy.common.domain.repository.mq.impl.RabbitBaseMqConsumer;
import com.evy.common.domain.repository.mq.model.MqSendMessage;
import com.evy.common.infrastructure.common.command.JsonUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * 记录交易日志
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/24 20:07
 */
@Component
public class TraceLogEvent extends BasicRabbitMqConsumer {
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
    protected int execute(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        MqSendMessage sendMessage = (MqSendMessage) SerializationUtils.deserialize(body);
        String messageJson = String.valueOf(sendMessage.getMessage());
        Map<String, String> map = JsonUtils.convertToObject(messageJson, Map.class);

        int isSuccess = DBUtils.executeUpdateSql(preparedStatement -> {
            try {
                preparedStatement.setString(1, map.getOrDefault(SRCSENDNO, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(2, map.getOrDefault(CODE, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(3, map.getOrDefault(CLIENTIP, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(4, map.getOrDefault(SERVERIP, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(5, map.getOrDefault(REQCONTENT, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(6, map.getOrDefault(ERRORCODE, BusinessConstant.EMPTY_STR));
                preparedStatement.setString(7, map.getOrDefault(ERRORMSG, BusinessConstant.EMPTY_STR));
                preparedStatement.execute();

                return BusinessConstant.SUCESS;
            } catch (SQLException e) {
                CommandLog.errorThrow("执行TraceLog异常", e);
                return BusinessConstant.FAILED;
            }
        }, INSERT_LOG);

        CommandLog.info("记录到日志流水结果:{}", isSuccess == 0 ? "成功" : "失败");

        return isSuccess;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        doExecute(consumerTag, envelope, properties, body);
    }
}
