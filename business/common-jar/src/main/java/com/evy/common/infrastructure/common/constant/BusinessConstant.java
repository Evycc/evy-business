package com.evy.common.infrastructure.common.constant;

/**
 * 常量字段及静态方法
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public class BusinessConstant {
    private BusinessConstant(){}
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final int SUCESS = 0;
    public static final int FAILED = 1;
    public static final int UNKNOW = 2;
    public static final String EMPTY_STR = "";
    public static final String WHITE_EMPTY_STR = " ";
    public static final String SPLIT_LINE = "\\|";
    public static final String LINE = "|";
    public static final String SPLIT_POINT = "\\.";
    public static final String POINT = ".";
    public static final String ENC_PRE_STR = "ENC(";

    /**
     * 可用cpu数量
     */
    public static final int CORE_CPU_COUNT = Runtime.getRuntime().availableProcessors();

//    /**
//     * 对应根据字段名从实例获取对应Field字段
//     * @param target    实例
//     * @param fieldName 字段名
//     * @return 返回java.lang.reflect.Field类型，不存在返回null
//     * @throws NoSuchFieldException
//     */
//    private static Field getFieldByObject(Object target, String fieldName) throws NoSuchFieldException {
//        try {
//            Field field = null;
//            for (Class tclass = target.getClass(); tclass != Object.class; tclass = tclass.getSuperclass()) {
//                field = tclass.getDeclaredField(fieldName);
//                if (field != null) {
//                    break;
//                }
//            }
//            return field;
//        } catch (NoSuchFieldException e) {
//            CommandLog.errorThrow("反射获取字段异常:{}", e);
//            throw e;
//        }
//    }
//
//    /**
//     * 对应根据字段名从实例获取对应值
//     * @param target    实例
//     * @param fieldName 字段名
//     * @return  字段值
//     * @throws IllegalAccessException   反射字段获取异常
//     * @throws NoSuchFieldException     不存在的字段
//     */
//    public static Object getValueByField(Object target, String fieldName) throws IllegalAccessException, NoSuchFieldException {
//        try {
//            Object value = null;
//            Field field = getFieldByObject(target, fieldName);
//
//            value = getValueByField(target, field);
//
//            return value;
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            CommandLog.errorThrow("反射获取字段异常:{}", e);
//            throw e;
//        }
//    }
//
//    /**
//     * 从实例中获取字段对应值
//     * @param target    实例
//     * @param field java.lang.reflect.Field
//     * @return  字段值，不存在返回null
//     * @throws IllegalAccessException   获取字段值异常
//     */
//    public static Object getValueByField(Object target, Field field) throws IllegalAccessException {
//        try {
//            Object value = null;
//            CommandUtils.fieldAccessSet(field, target, value);
//
//            return value;
//        } catch (IllegalAccessException e) {
//            CommandLog.errorThrow("反射获取字段异常:{}", e);
//            throw e;
//        }
//    }
}
