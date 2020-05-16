package com.evy.common.infrastructure.common.command.utils;

import com.evy.common.domain.repository.factory.CreateFactory;
import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.common.log.CommandLog;
import lombok.Getter;
import lombok.Setter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用工具类
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/27 23:33
 */
public class CommandUtils {
    private static final StandardPBEStringEncryptor STANDARD_PBE_STRING_ENCRYPTOR = new StandardPBEStringEncryptor();
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService("CommandUtils- ExecutorService");
    private static long EXECUTE_TIME_OUT = 30000;
    private static final String TIMEOUT_ERRORCODE = "TIMEOUT_ERR";
    private static final String TIMEOUT_ERRMSG = "接口调用超时";
    private static BusinessPrpoties prpoties;
    /**
     * lambda表达式内获取值用
     */
    @Getter
    @Setter
    private static Map<Integer, Object> tempObject = new HashMap<>();

    static {
        CommandLog.info("初始化CommandUtils");
        initPrpo();
        //密钥
        STANDARD_PBE_STRING_ENCRYPTOR.setPassword("evy-key");
        //默认加密算法
        STANDARD_PBE_STRING_ENCRYPTOR.setAlgorithm("PBEWithMD5AndDES");
    }

    /**
     * 设置临时变量，仅限同一个线程
     * @param objClass  获取临时变量的类对象
     * @param obj   临时变量
     */
    public static void setLambdaTemp(Object objClass, Object obj){
        tempObject.put(Long.valueOf(Thread.currentThread().getId()).intValue() + objClass.hashCode(), obj);
    }

    /**
     * 获取临时变量
     * @param objClass  获取临时变量的类对象
     * @return  临时变量
     */
    public static Object getLambdaTemp(Object objClass){
        int key = objClass.hashCode() + Long.valueOf(Thread.currentThread().getId()).intValue();
        return getLambdaTemp(key);
    }

    /**
     * 获取临时变量并删除
     * @param objClass  获取临时变量的类对象
     * @return  临时变量
     */
    public static Object getLambdaTempAndDel(Object objClass){
        int key = objClass.hashCode() + Long.valueOf(Thread.currentThread().getId()).intValue();
        Object value = tempObject.get(key);
        tempObject.remove(key);
        return value;
    }

    /**
     * 从tempObject中获取临时变量
     * @param key   类对象的hasCode
     * @return  类对象对应临时变量
     */
    private static Object getLambdaTemp(int key){
        if (tempObject.containsKey(key)){
            return tempObject.get(key);
        }
        return null;
    }

    private static void initPrpo() {
        EXECUTOR_SERVICE.submit(() -> {
            try {
                prpoties = AppContextUtils.getBean(BusinessPrpoties.class);
            } catch (Exception e) {
                CommandLog.errorThrow("CommandUtils#execute初始化异常", e);
            } finally {
                if (prpoties != null) {
                    EXECUTE_TIME_OUT = prpoties.getExecuteTimeout();
                }
                CommandLog.info("CommandUtils#execute默认超时时间{}ms", EXECUTE_TIME_OUT);
            }
        });
    }

    /**
     * 执行方法
     *
     * @param obeject           执行对象
     * @param function          执行逻辑
     * @param exceptionConsumer 异常处理
     * @param timeout           超时时间，单位ms
     * @param <T>               对象类型
     * @return 0成功，1失败
     */
    public static <T> int execute(T obeject, Function<T, Integer> function, Consumer<BasicException> exceptionConsumer, int timeout) {
        CommandLog.info("Start Service");
        int returnCode;
        long s = System.currentTimeMillis();
        try {
            if (timeout > 0) {
                returnCode = Executors.newScheduledThreadPool(1).submit(() -> function.apply(obeject))
                        .get(timeout, TimeUnit.MILLISECONDS);
            }
            else {
                if (prpoties == null) {
                    initPrpo();
                }
                returnCode = EXECUTOR_SERVICE.submit(() -> function.apply(obeject))
                        .get(EXECUTE_TIME_OUT, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            exceptionConsumer.accept(new BasicException(TIMEOUT_ERRORCODE, TIMEOUT_ERRMSG, e));
            returnCode = BusinessConstant.FAILED;
        } catch (Exception e) {
            exceptionConsumer.accept(new BasicException(e));
            returnCode = BusinessConstant.FAILED;
        }
        CommandLog.info("End Service({}ms)", System.currentTimeMillis() - s);
        return returnCode;
    }

    /**
     * 将map转化为dto
     *
     * @param dtoName dto类型
     * @param fromMap 键值对map
     * @return dto实例
     */
    public static Object conveterFromMap(String dtoName, Map<String, String> fromMap) {
        Object dto = null;
        try {
            dto = Class.forName(dtoName).getDeclaredConstructor().newInstance();
            Class temp = dto.getClass();

            Field[] fields = getAllField(temp);
            for (Field field : fields) {
                String key = field.getName();
                if (fromMap.containsKey(key)) {
                    fieldAccessSet(field, dto, fromMap.get(key));
                }
            }
        } catch (Exception e) {
            CommandLog.errorThrow("转换dto异常", e);
        }
        return dto;
    }

    /**
     * 将dto转为map
     * @param from  源对象
     * @param to    转化后对象
     */
    public static void conveterFromDto(Object from, Map to){
        Field[] fields = getAllField(from.getClass());
        try {
            for (Field field : fields) {
                to.put(field.getName(), String.valueOf(fieldAccessGet(field, from)));
            }
        } catch (IllegalAccessException e) {
            CommandLog.errorThrow("反射获取字段异常", e);
        }
    }

    /**
     * 获取指定类所有字段
     * @param cls   源类
     * @return java.lang.reflect.Field数组
     */
    public static Field[] getAllField(Class cls){
        List<Field> fieldList = new ArrayList<>();
        while (cls != Object.class) {
            Field[] fields = cls.getDeclaredFields();
            cls = cls.getSuperclass();

            fieldList.addAll(Arrays.asList(fields));
        }

        return fieldList.toArray(new Field[0]);
    }

    /**
     * 返回dto字段(包含父类)
     * @param dto   实例
     * @return  字段字符串
     */
    public static String returnDtoParam(Object dto) {
        try {
            Objects.requireNonNull(dto);
            StringBuffer str = new StringBuffer();
            Class temp = dto.getClass();
            String clsName = temp.getName();
                    str.append("[")
                    .append(clsName.substring(clsName.lastIndexOf(".") +1))
                    .append("(");

            Field[] fields = getAllField(temp);
            for (Field field : fields) {
                str.append(field.getName())
                        .append("=")
                        .append(fieldAccessGet(field, dto))
                        .append(",");
            }

            int l = str.lastIndexOf(",");
            if (l > 0) {
                str.delete(l, str.length());
            }
            str.append(")")
                    .append("]");
            return str.toString();
        } catch (IllegalAccessException e) {
            CommandLog.errorThrow("获取DTO字段异常", e);
            return BusinessConstant.EMPTY_STR;
        }
    }

    /**
     * jasypt解密
     * @param pass  加密字符串
     * @return  解密后字符串
     */
    public static String decode(String pass){
        String result = "";
        try {
            result = STANDARD_PBE_STRING_ENCRYPTOR.decrypt(pass);
            return result;
        } catch (Exception e){
            CommandLog.errorThrow("", e);
            return BusinessConstant.EMPTY_STR;
        }
    }

    /**
     * 解密ENC开头的字符串
     * @param pass  ENC(密码串)
     * @return  解密后字符串
     */
    public static String decodeEnc(String pass){
        //解密ENC开头的密码
        pass = pass.substring(4, pass.length() -1);
        pass = CommandUtils.decode(pass);
        return pass;
    }

    /**
     * jasypt加密
     * @param pass  字符串
     * @return  加密后字符串
     */
    public static String encodeEnc(String pass){
        String result = "";
        try {
            result = STANDARD_PBE_STRING_ENCRYPTOR.encrypt(pass);
            return result;
        } catch (Exception e){
            CommandLog.errorThrow("", e);
            return BusinessConstant.EMPTY_STR;
        }
    }

    /**
     * 获取Field值
     * @param field java.lang.reflect.Field
     * @param obj   源获取对象
     * @return java.lang.Object
     */
    public static Object fieldAccessGet(Field field, Object obj) throws IllegalAccessException {
        Object value;
        if (!field.canAccess(obj)) {
            field.setAccessible(true);
            value = field.get(obj);
            field.setAccessible(false);
        } else {
            value = field.get(obj);
        }
        return value;
    }

    /**
     * 反射赋值
     * @param field java.lang.reflect.Field
     * @param obj   赋值对象
     * @param value 字段值
     */
    public static void fieldAccessSet(Field field, Object obj, Object value) throws IllegalAccessException {
        if (!field.canAccess(obj)) {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } else {
            field.set(obj, value);
        }
    }

    /**
     * 对应根据字段名从实例获取对应Field字段
     * @param target    实例
     * @param fieldName 字段名
     * @return 返回java.lang.reflect.Field类型，不存在返回null
     * @throws NoSuchFieldException
     */
    private static Field getFieldByObject(Object target, String fieldName) throws NoSuchFieldException {
        try {
            Field field = null;
            for (Class tclass = target.getClass(); tclass != Object.class; tclass = tclass.getSuperclass()) {
                field = tclass.getDeclaredField(fieldName);
                if (field != null) {
                    break;
                }
            }
            return field;
        } catch (NoSuchFieldException e) {
            CommandLog.errorThrow("反射获取字段异常:{}", e);
            throw e;
        }
    }

    /**
     * 对应根据字段名从实例获取对应值
     * @param target    实例
     * @param fieldName 字段名
     * @return  字段值
     * @throws IllegalAccessException   反射字段获取异常
     * @throws NoSuchFieldException     不存在的字段
     */
    public static Object getValueByField(Object target, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        try {
            Object value = null;
            Field field = getFieldByObject(target, fieldName);

            value = getValueByField(target, field);

            return value;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            CommandLog.errorThrow("反射获取字段异常:{}", e);
            throw e;
        }
    }

    /**
     * 从实例中获取字段对应值
     * @param target    实例
     * @param field java.lang.reflect.Field
     * @return  字段值，不存在返回null
     * @throws IllegalAccessException   获取字段值异常
     */
    public static Object getValueByField(Object target, Field field) throws IllegalAccessException {
        try {
            Object value = null;
            CommandUtils.fieldAccessSet(field, target, value);

            return value;
        } catch (IllegalAccessException e) {
            CommandLog.errorThrow("反射获取字段异常:{}", e);
            throw e;
        }
    }
}
