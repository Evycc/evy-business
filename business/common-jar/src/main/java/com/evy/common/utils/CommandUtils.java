package com.evy.common.utils;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.config.BusinessProperties;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.log.CommandLog;
import lombok.Getter;
import lombok.Setter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
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
    private static final ExecutorService EXECUTOR_SERVICE = CreateFactory.returnExecutorService(CommandUtils.class.getSimpleName());
    private static long EXECUTE_TIME_OUT = 30000;
    private static final String TIMEOUT_ERRORCODE = "TIMEOUT_ERR";
    private static final String TIMEOUT_ERRMSG = "接口调用超时";
    private static BusinessProperties prpoties;
    /**
     * lambda表达式内获取值用
     */
    @Getter
    @Setter
    private static volatile Map<Integer, Object> tempObject = new HashMap<>();

    static {
        CommandLog.info("初始化CommandUtils");
        initPrpo();
        //密钥
        STANDARD_PBE_STRING_ENCRYPTOR.setPassword("evy-key");
        //默认加密算法
        STANDARD_PBE_STRING_ENCRYPTOR.setAlgorithm("PBEWithMD5AndDES");
    }

    private static void initPrpo() {
        try {
            prpoties = AppContextUtils.getPrpo();
        } catch (Exception e) {
            CommandLog.errorThrow("CommandUtils#execute初始化异常", e);
        } finally {
            if (prpoties != null) {
                EXECUTE_TIME_OUT = prpoties.getExecuteTimeout();
            }
            CommandLog.info("CommandUtils#execute默认超时时间{}ms", EXECUTE_TIME_OUT);
        }
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
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
                CreateFactory.createThreadFactory("CommandUtils#execute"));

        try {
            if (timeout > BusinessConstant.ZERO_NUM) {
                returnCode = executor.submit(() -> function.apply(obeject))
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
        executor = null;
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
            Class<?> temp = dto.getClass();

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
    public static void conveterFromDto(Object from, Map<String, Object> to){
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
     * jasypt解密
     * @param pass  加密字符串
     * @return  解密后字符串
     */
    public static String decode(String pass){
        String result = BusinessConstant.EMPTY_STR;
        try {
            result = STANDARD_PBE_STRING_ENCRYPTOR.decrypt(pass);
        } catch (Exception e){
            CommandLog.errorThrow(BusinessConstant.EMPTY_STR, e);
        }
        return result;
    }

    /**
     * 解密ENC开头的字符串
     * @param pass  ENC(密码串)
     * @return  解密后字符串
     */
    public static String decodeEnc(String pass){
        if (pass.startsWith(BusinessConstant.ENC_PRE_STR)) {
            //解密ENC开头的密码
            pass = pass.substring(4, pass.length() -1);
            pass = CommandUtils.decode(pass);
        }

        return pass;
    }

    /**
     * jasypt加密
     * @param pass  字符串
     * @return  加密后字符串
     */
    public static String encodeEnc(String pass){
        String result = BusinessConstant.EMPTY_STR;
        try {
            result = STANDARD_PBE_STRING_ENCRYPTOR.encrypt(pass);
        } catch (Exception e){
            CommandLog.errorThrow(BusinessConstant.EMPTY_STR, e);
        }
        return result;
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
    public static Field getFieldByObject(Object target, String fieldName) throws NoSuchFieldException {
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
            throw e;
        }
    }
}
