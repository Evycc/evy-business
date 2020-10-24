package com.evy.common.utils;

import com.evy.common.command.infrastructure.config.BusinessPrpoties;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * 获取Spring ApplicationContext
 * 注意: 使用@SpringBootApplication(scanBasePackages = "com.evy.*")扫描该路径，以注入ApplicationContext
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/9 15:32
 */
@Component("appContextUtils")
public class AppContextUtils implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;
    private static Environment ENVIRONMENT;
    private static BusinessPrpoties BUSINESS_PRPOTIES;
    private static final Map<String, String> PRPO_TEMP_MAP = new HashMap<>(16);

    public AppContextUtils(final BusinessPrpoties businessPrpoties) {
        BUSINESS_PRPOTIES = businessPrpoties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return getApplicationContext().getBean(beanClass);
    }

    public static ApplicationContext getApplicationContext() {
        return CONTEXT;
    }

    public static Environment getEnv() {
        return ENVIRONMENT;
    }

    public static String getForEnv(String param) {
        String result = BusinessConstant.EMPTY_STR;
        try {
            if (ENVIRONMENT == null) {
                ENVIRONMENT = getBean(Environment.class);
            }
            result = ENVIRONMENT.getProperty(param);
        } catch (Exception exception) {
            if (StringUtils.isEmpty(result)) {
                result = getPrpoFromTempMap(param);
            }
        }

        return result;
    }

    public static BusinessPrpoties getPrpo() {
        return BUSINESS_PRPOTIES;
    }

    /**
     * 扫描项目resource目录下的配置
     * @param param 配置项
     * @return  配置
     */
    private static String getPrpoFromTempMap(String param) {
        if (CollectionUtils.isEmpty(PRPO_TEMP_MAP)) {
            String postProperties = ".properties";
            String postYml = ".yml";
            List<String> lists = getFilterFilePath(getResourcePath(), name ->
                    name.contains(postProperties) || name.contains(postYml));

            if (!CollectionUtils.isEmpty(lists)) {
                Properties properties = new Properties();
                Yaml yaml = new Yaml();
                lists.forEach(url -> {
                    try (FileInputStream fileInputStream = new FileInputStream(new File(url))) {
                        if (url.contains(postProperties)) {
                            properties.load(fileInputStream);
                            properties.forEach((key, value) -> PRPO_TEMP_MAP.put(String.valueOf(key), String.valueOf(value)));
                        } else {
                            Map<String, String> temp = yaml.loadAs(fileInputStream, Map.class);
                            PRPO_TEMP_MAP.putAll(temp);
                        }

                    } catch (Exception ignore) {
                    }
                });
            }
        }
        return PRPO_TEMP_MAP.get(param);
    }

    /**
     * 获取Resource路径集合
     * @return java.util.List
     */
    public static List<URL> getResourcePath() {
        List<URL> urls = new ArrayList<>(8);

        try {
            Enumeration<URL> enumeration = AppContextUtils.class.getClassLoader().getResources(File.separator);
            while (enumeration.hasMoreElements()) {
                urls.add(enumeration.nextElement());
            }
        } catch (IOException e) {
            CommandLog.error("AppContextUtils#getResourcePath获取Resouce路径异常");
        }

        return urls;
    }

    /**
     * 从Resource获取指定格式文件
     * @param resources Resource路径集合
     * @param filter 过滤条件
     * @return
     */
    private static List<String> getFilterFilePath(List<URL> resources, Predicate<String> filter) {
        List<String> result = new ArrayList<>(8);

        for (int i = 0; i < resources.size(); i++) {
            try {
                File temp = new File(resources.get(i).toURI());
                List<String> temp1 = getFilterFile(temp, filter);
                result.addAll(temp1);
            } catch (URISyntaxException e) {
                CommandLog.error("AppContextUtils#getFilterFilePath获取Resouce文件异常");
            }
        }

        return result;
    }

    /**
     * 遍历获取文件路径下指定文件
     * @param file  源文件路径
     * @param filter    过滤规则
     * @return  指定文件路径
     */
    public static List<String> getFilterFile(File file, Predicate<String> filter) {
        List<String> result = new ArrayList<>(8);
        if (file.isFile() && filter.test(file.getName())) {
            result.add(file.getPath());
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isFile() && filter.test(file1.getName())) {
                        result.add(file1.getPath());
                    } else {
                        List<String> var1 = getFilterFile(file1, filter);
                        if (!var1.isEmpty()) {
                            result.addAll(var1);
                        }
                    }
                }
            }
        }

        return result;
    }
}
