package com.evy.common.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * JSON工具类
 * @Author: EvyLiuu
 * @Date: 2020/1/24 11:18
 */
public class JsonUtils {
    private static final Gson FINAL_GSON = new Gson();

    public static String convertToJson(Object object, Type cls) {
        return FINAL_GSON.toJson(object, cls);
    }

    public static String convertToJson(Object object) {
        return FINAL_GSON.toJson(object);
    }

    public static <T> T convertToObject(String jsonStr, Class<T> cls) {
        return FINAL_GSON.fromJson(jsonStr, cls);
    }
}
