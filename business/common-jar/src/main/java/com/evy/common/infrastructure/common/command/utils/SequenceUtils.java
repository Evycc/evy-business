package com.evy.common.infrastructure.common.command.utils;

import com.evy.common.infrastructure.common.constant.BusinessConstant;

/**
 * 基于雪花算法的唯一序列，最大64bit
 * 时间戳(36bit)+机器进程ID(12bit)+MAC地址(12bit)+sequence(4bit)
 * @Author: EvyLiuu
 * @Date: 2020/5/16 16:03
 */
public class SequenceUtils {
    /**
     * 809890920260L 1995-9-1
     * 13位，可以撑到2286年
     */
    private static final long TWEPOCH = 809890920260L;

    /**
     * 时间戳占用位数
     */
    private static final long TIMESTAMP_LEFT_SHIFT = 36L;

    /**
     * MAC地址占用位数
     */
    private static final long PID_LEFT_SHIFT = 12L;

    /**
     * 4095，限制sequence最大值，当sequence达到sequenceMask上限时，sequence取1
     */
    private static final long SEQUENCE_MASK = ~(-1L << 12L);

    /**
     * 毫秒内序列(0~sequenceMask)
     */
    private static long SEQUENCE = 0L;

    /**
     * 记录上次生成ID的时间截
     */
    private static long LAST_TEMP_STAMP = -1L;

    private static long PID_HASHCODE = BusinessConstant.WORK_PID;

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < LAST_TEMP_STAMP) {
            throw new RuntimeException(
                    String.format("系统时间小于LAST_TEMP_STAMP记录时间，序列生成异常 %s", LAST_TEMP_STAMP - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (LAST_TEMP_STAMP == timestamp) {
            SEQUENCE = (SEQUENCE + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (SEQUENCE == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(LAST_TEMP_STAMP);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            SEQUENCE = 0L;
        }

        //上次生成ID的时间截
        LAST_TEMP_STAMP = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)  //时间戳部分
                | PID_HASHCODE << PID_LEFT_SHIFT //机器PID
                | BusinessConstant.MAC_ID << PID_LEFT_SHIFT // MAC
                | SEQUENCE; //序列号部分
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取指定前缀的唯一序列
     * @param prefix    前缀
     * @return  唯一序列
     */
    public static String getPrefix(String prefix) {
        return prefix.concat(String.valueOf(nextId()));
    }

    /**
     * 获取指定前缀，指定长度的唯一序列
     * @param prefix    前缀
     * @param len   长度
     * @return  唯一序列
     */
    public static String getPrefix(String prefix, int len) {
        String val = String.valueOf(nextId());
        int n = val.length() - len;
        if (n <= 0) {
            throw new RuntimeException("len长度大于序列 len:{}" +len);
        }
        return prefix.concat(val.substring(n));
    }
}
