package com.evy.common.infrastructure.common.command.utils;

import com.evy.common.infrastructure.common.constant.BusinessConstant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 *
 * @Author: EvyLiuu
 * @Date: 2020/5/6 23:55
 */
public class DateUtils {
    public static final String YYYYMMDD = "yyyyMMDD";
    public static final String YYYY_MM_DD = "yyyy-MM-DD";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY = "yyyy";
    public static final String MM_MONTH = "MM";
    public static final String DD = "DD";
    public static final String HH = "HH";
    public static final String MM_MINUTE = "mm";
    public static final String SS = "ss";
    public static final String DATE_SPLIT1 = "-";
    public static final String DATE_SPLIT2 = ":";
    public static final String YYYY_MM_DD_HH_MM_SS = YYYYMMDD + BusinessConstant.WHITE_EMPTY_STR + HH_MM_SS;

    /**
     * yyyyMMDD HH:mm:ss
     */
    public static DateTimeFormatter PATTERN1 = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    /**
     * yyyy-MM-DD
     */
    public static DateTimeFormatter PATTERN2 = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    public static LocalDateTime parse(String text, String pattern) {
        return LocalDateTime.parse(text, DateUtils.ofPattern(pattern));
    }

    public static Duration between(LocalDateTime date1, LocalDateTime date2) {
        return Duration.between(date1, date2);
    }

    public static LocalDateTime plus(LocalDateTime date, long plus, DATE_TYPE dateType) {
        switch (dateType) {
            case YEARS:
                return date.plusYears(plus);
            case MONTHS:
                return date.plusMonths(plus);
            case DAYS:
                return date.plusDays(plus);
            case WEEKS:
                return date.plusWeeks(plus);
            case HOURS:
                return date.plusHours(plus);
            case MINUTES:
                return date.plusMinutes(plus);
            case SECONDS:
                return date.plusMinutes(plus);
            case NANOS:
                return date.plusNanos(plus);
            default:
                return LocalDateTime.now();
        }
    }

    public static String plus(LocalDateTime date, long plus, DATE_TYPE dateType, String pattern) {
        return plus(date, plus, dateType).format(ofPattern(pattern));
    }

    public enum DATE_TYPE {
        /**
         * 年
         */
        YEARS,
        /**
         * 月
         */
        MONTHS,
        /**
         * 周
         */
        WEEKS,
        /**
         * 天
         */
        DAYS,
        /**
         * 小时
         */
        HOURS,
        /**
         * 分钟
         */
        MINUTES,
        /**
         * 秒
         */
        SECONDS,
        /**
         * 微秒
         */
        NANOS,
    }
}
