package com.evy.common.infrastructure.config;

import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 统一初始化工具
 * @Author: EvyLiuu
 * @Date: 2020/5/16 12:25
 */
@Component
public class CommandInitialize implements CommandLineRunner {
    private static List<InitModel> eventList = Collections.synchronizedList(new ArrayList<>(8));
    public static final String INIT_STATIC_METHOD = "init";

    @Override
    public void run(String... args) throws Exception {
        CommandLog.info("CommandInitialize执行初始化...");
        for (InitModel initModel : eventList) {
            Class<?> cls = initModel.getCls();
            Method method = initModel.getInitMethod();
            method.setAccessible(true);
            try {
                method.invoke(null);
            } catch (IllegalAccessException e) {
                CommandLog.error("无权限执行该方法 cls:{} method:{}", cls, e);
                throw e;
            } catch (InvocationTargetException e) {
                CommandLog.error("执行初始化方法异常 cls:{} method:{}", cls, e);
                throw e;
            }
        }
        CommandLog.info("CommandInitialize执行初始化结束");
    }

    /**
     * 添加初始化事件
     * @param cls   初始化类
     * @param initMethod   初始化类对应静态方法
     */
    public static void addStaticInitEvent(Class<?> cls, String initMethod){
        try {
            eventList.add(InitModel.create(cls, cls.getDeclaredMethod(initMethod)));
        } catch (NoSuchMethodException e) {
            CommandLog.info("addInitEvent不存在方法 cls:{} method:{}", cls, initMethod);
        }
    }

    @Getter
    @Setter
    private static class InitModel {
        Class<?> cls;
        Method initMethod;
        Object[] objects;

        private InitModel(Class<?> cls, Method initMethod, Object[] objects) {
            this.cls = cls;
            this.initMethod = initMethod;
            this.objects = objects;
        }

        private static InitModel create(Class<?> cls, Method initMethod, Object... objects) {
            return new InitModel(cls, initMethod, objects);
        }
    }
}
