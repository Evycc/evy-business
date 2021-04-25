package com.evy.common.batch;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.database.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.MqSender;
import com.evy.common.mq.rabbitmq.app.basic.BaseBatchRabbitMqConsumer;
import com.evy.common.utils.AppContextUtils;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批次工具类
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/23 20:34
 */
public class BatchUtils {
    private static final String QRY_BATCH_LIST = "SELECT tdb_batch_name,tdb_batch_topic,tdb_batch_tag FROM td_batch";
    private static final String UPDATE_START_DATE = "UPDATE td_batch SET tdb_start_time = NOW() WHERE tdb_batch_name = ?";
    private static final String UPDATE_END_DATE = "UPDATE td_batch SET tdb_end_time = NOW() WHERE tdb_batch_name = ?";
    /**
     * 存储批次信息Map集合 K:批次信息 V:批次信息值
     */
    private static List<Map<String, String>> BATCH_LIST = new ArrayList<>();
    private static final String BATCH_NAME = "tdb_batch_name";
    private static final String BATCH_TOPIC = "tdb_batch_topic";
    private static final String BATCH_TAG = "tdb_batch_tag";
    private static final MqSender MQ_SENDER = AppContextUtils.getBean("RabbitMqSender");

    //初始化td_batch表中batch信息
    static {
        CommandLog.info("初始化批次Map");
        DBUtils.executeQuerySql(resultSet -> {
            try {
                while (resultSet.next()) {
                    Map<String, String> map = new HashMap<>(3);
                    map.put(BATCH_NAME, resultSet.getString(1));
                    map.put(BATCH_TOPIC, resultSet.getString(2));
                    map.put(BATCH_TAG, resultSet.getString(3));
                    BATCH_LIST.add(map);
                }
            } catch (SQLException e) {
                CommandLog.errorThrow("初始化Batch信息失败", e);
            }
        }, QRY_BATCH_LIST, null);
    }

    /**
     * 产生批次MQ<br/>
     * 批次消费者{@link BaseBatchRabbitMqConsumer}
     * @param batchName 批次名
     * @param cid   消费者tag
     * @return  0成功，1失败
     */
    public static int providerBatch(String batchName, String cid) {
        return providerBatch(batchName, cid, "");
    }

    /**
     * 产生批次MQ<br/>
     * 批次消费者{@link BaseBatchRabbitMqConsumer}
     * @param batchName 批次名
     * @param cid   消费者tag
     * @param msg   message
     * @return  0成功，1失败
     */
    public static int providerBatch(String batchName, String cid, Object msg) {
        int rs = 1;
        Map<String, String> map = getMapForBatchName(batchName);
        if (CollectionUtils.isEmpty(map)) {
            CommandLog.error("批次执行失败，批次Map为空");
            return rs;
        }

        if (updateDateSql(batchName, UPDATE_START_DATE) == BusinessConstant.FAILED) {
            CommandLog.error("更新批次时间失败");
        }

        rs = MQ_SENDER.sendAndConfirm(map.get(BATCH_TOPIC), map.get(BATCH_TAG), cid, msg);

        return rs;
    }

    /**
     * 更新批次执行时间
     * @param batchName 批次名
     * @param sql   执行sql
     * @return  0成功，1失败
     */
    private static int updateDateSql(String batchName, String sql) {
        int result = DBUtils.executeUpdateSql(sql, new ArrayList<>(){{add(batchName);}});
        if (result != BusinessConstant.SUCESS) {
            CommandLog.error("更新Batch执行时间异常");
        }
        return result;
    }

    /**
     * 更新批次执行结束时间
     * @param batchName 批次名
     * @return  0成功，1失败
     */
    public static int updateEndDateSql(String batchName) {
        return updateDateSql(batchName, UPDATE_END_DATE);
    }

    /**
     * 获取batchName对应Map
     * @param batchName batch对应批次名
     * @return  存储这batch对应topic信息的map
     */
    private static Map<String, String> getMapForBatchName(String batchName) {
        if (CollectionUtils.isEmpty(BATCH_LIST)) {
            return null;
        }
        for (Map<String, String> map : BATCH_LIST) {
            if (!CollectionUtils.isEmpty(map) && map.containsValue(batchName)) {
                return map;
            }
        }
        return null;
    }

    /**
     * 根据topic查找对应batchName
     * @param topic topic
     * @return  batchName
     */
    public static String qryTopicFromBatchName(String topic) {
        if (CollectionUtils.isEmpty(BATCH_LIST)) {
            return null;
        }
        for (Map<String, String> map : BATCH_LIST) {
            if (!CollectionUtils.isEmpty(map) && map.containsValue(topic)) {
                return map.get(BATCH_NAME);
            }
        }
        return null;
    }
}
