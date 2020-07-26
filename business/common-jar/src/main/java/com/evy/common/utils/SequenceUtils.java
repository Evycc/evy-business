package com.evy.common.utils;

import com.evy.common.command.infrastructure.constant.BusinessConstant;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * 基于雪花算法的唯一序列，最大64bit
 * 符号位(1bit)+时间戳(35bit)+机器进程ID(12bit)+MAC地址(12bit)+sequence(4bit)
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
    private static final long TIMESTAMP_LEFT_SHIFT = 35L;

    /**
     * MAC地址占用位数
     */
    private static final long PID_LEFT_SHIFT = 12L;

    /**
     * 4095，限制sequence最大值，当sequence达到sequenceMask上限时，sequence取1
     */
    private static final long SEQUENCE_MASK = ~(-1L << PID_LEFT_SHIFT);

    /**
     * 毫秒内序列(0~sequenceMask)
     */
    private static long SEQUENCE = 0L;

    /**
     * 记录上次生成ID的时间截
     */
    private static long LAST_TIME_STAMP = -1L;

    /**
     * 应用内时间,随机产生
     */
    private static final long CUR_TIME_STAMP = LocalDateTime.of(new Random().nextInt(54) +1998,
            new Random().nextInt(12) +1,new Random().nextInt(27) +1,
            new Random().nextInt(23) +1,new Random().nextInt(60) +1,
            new Random().nextInt(60) +1).toInstant(ZoneOffset.of("+8")).toEpochMilli();

    /**
     * 机器ID
     */
    private static final long PID_HASHCODE = BusinessConstant.WORK_PID;

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();

        //如果是同一时间生成的，则进行毫秒内序列
        if (LAST_TIME_STAMP == timestamp) {
            SEQUENCE = (SEQUENCE + 1) & SEQUENCE_MASK;
            //毫秒内序列溢出
            if (SEQUENCE == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(LAST_TIME_STAMP);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            SEQUENCE = 0L;
        }

        //上次生成ID的时间截
        LAST_TIME_STAMP = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return  ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)  //时间戳部分
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
        return CUR_TIME_STAMP + BusinessConstant.getVmUpTime();
    }

    /**
     * 返回无符号序列
     * @return 无符号序列
     */
    public static String getNextSeq(){
        return Long.toUnsignedString(nextId());
    }

    /**
     * 获取指定前缀的唯一序列
     * @param prefix    前缀
     * @return  唯一序列
     */
    public static String getPrefix(String prefix) {
        return prefix.concat(getNextSeq());
    }

    /**
     * 获取指定前缀，指定长度的唯一序列
     * @param prefix    前缀
     * @param len   长度
     * @return  唯一序列
     */
    public static String getPrefix(String prefix, int len) {
        return fillToLen(prefix.concat(getNextSeq()), len);
    }

    /**
     * 返回指定长度序列
     * @param len   序列长度
     * @return  唯一序列
     */
    public static String nextId(int len) {
        return fillToLen(getNextSeq(), len);
    }

    /**
     * 填充或者截取字符串
     * @param str   源字符串
     * @param len   填充或截取直到指定长度
     * @return  指定长度字符串
     */
    private static String fillToLen(String str, int len) {
        while (str.length() < len) {
            str = str.concat(BusinessConstant.ZERO);
        }
        if (str.length() > len) {
            str = str.substring(str.length() -len);
        }
        return str;
    }
}
