package com.evy.common.trace.service;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.db.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.model.TraceServiceModel;
import com.evy.common.trace.infrastructure.tunnel.po.TraceServiceBeanAndConsumerPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceServiceUpdateListPO;
import com.evy.common.trace.infrastructure.tunnel.po.TraceServiceUpdatePO;
import com.evy.common.utils.AppContextUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * 获取应用发布服务信息<br/>
 * 配置 : 定时刷新服务列表 evy.trace.service.timing.flag={0开启|1关闭}<br/>
 *
 * @Author: EvyLiuu
 * @Date: 2020/7/8 20:32
 */
public class TraceService {
    private static final String SERVICE_TIMING_PRPO = "evy.trace.service.timing.flag";
    private static final ConcurrentLinkedQueue<TraceServiceModel> SERVICES_INFO_LIST = new ConcurrentLinkedQueue<>();
    /**
     * bean与消费者map对象 k:beanName v:consumer
     */
    private static final Map<String, String> SERVICES_BEAN_NAME_CONSUMER_MAP = new HashMap<>(16);
    /**
     * bean与消费者map临时对象 k:beanName v:consumer
     */
    private static Map<String, String> SERVICES_BEAN_NAME_CONSUMER_TEMP_MAP = new HashMap<>(16);
    /**
     * 查找服务bean名称及对应服务者名称
     */
    private static final String QUERY_CONSUMERS = "com.evy.common.trace.repository.mapper.TraceMapper.queryServiceConsumers";
    /**
     * 新增或更新发布者信息
     */
    private static final String INSERT_SERVICES_BEAN = "com.evy.common.trace.repository.mapper.TraceMapper.insertServiceProdiverInfo";
    /**
     * 更新消费者信息
     */
    private static final String UPDATE_SERVICES_CONSUMER = "com.evy.common.trace.repository.mapper.TraceMapper.updateConsumerServiceInfo";
    /**
     * 清除发布者/消费者信息
     */
    private static final String UPDATE_CLEAN_IP = "com.evy.common.trace.repository.mapper.TraceMapper.cleanServiceByAppIp";
    private static final String QRY_ALL_SERVICE_BEAN = "com.evy.common.trace.repository.mapper.TraceMapper.queryAllServiceName";
    /**
     * 应用名
     */
    private static final String APP_NAME = getAppName();

    static {
        cleanServiceInfo();
    }

    /**
     * 应用结束时,清除发布者、消费者ip
     */
    public static void cleanServiceInfo() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String ip = buildProviderName(APP_NAME);
                //test
                CommandLog.info("shutdown service info ip:{}", ip);
                DBUtils.update(UPDATE_CLEAN_IP, TraceServiceUpdatePO.createCleanIp(ip, ip));
            } catch (Exception e) {
                CommandLog.errorThrow("cleanServiceInfo error", e);
            }
        }));
    }

    /**
     * 更新应用服务发布者、服务消费者信息
     */
    public static void executeService() {
        Optional.ofNullable(AppContextUtils.getForEnv(SERVICE_TIMING_PRPO))
                .ifPresent(flag -> {
                    if (BusinessConstant.ZERO.equals(flag)) {
                        SERVICES_BEAN_NAME_CONSUMER_TEMP_MAP = initConsumersMap();
                        if (!CollectionUtils.isEmpty(SERVICES_BEAN_NAME_CONSUMER_TEMP_MAP)) {
                            SERVICES_BEAN_NAME_CONSUMER_MAP.clear();
                            SERVICES_BEAN_NAME_CONSUMER_MAP.putAll(SERVICES_BEAN_NAME_CONSUMER_TEMP_MAP);
                            SERVICES_BEAN_NAME_CONSUMER_TEMP_MAP = null;
                        }
                    }
                });

        List<TraceServiceUpdatePO> poList = new ArrayList<>(8);
        List<TraceServiceUpdatePO> cList = new ArrayList<>(8);

        //更新消费者服务方信息
        String appName = buildProviderName(APP_NAME);
        //添加消费者信息
        if (!CollectionUtils.isEmpty(SERVICES_BEAN_NAME_CONSUMER_MAP)) {
            SERVICES_BEAN_NAME_CONSUMER_MAP.forEach((beanName, consumer) -> {
                if (consumer.contains(APP_NAME)) {
                    cList.add(TraceServiceUpdatePO.createUpdateConsumer(beanName, appName, consumer));
                }
            });
        }
        if (!CollectionUtils.isEmpty(cList)) {
            //更新消费者机器信息
            if (cList.size() > BusinessConstant.ONE_NUM) {
                //大于1,进行批量update
                DBUtils.batchAny(cList.stream()
                        .map(updatePo -> DBUtils.BatchModel.create(UPDATE_SERVICES_CONSUMER, updatePo, DBUtils.BatchType.UPDATE))
                        .collect(Collectors.toList())
                );
            } else {
                cList.forEach(updatePo -> DBUtils.update(UPDATE_SERVICES_CONSUMER, updatePo));
            }
        }

        //更新发布者服务方信息
        if (!CollectionUtils.isEmpty(SERVICES_INFO_LIST)) {
            try {
                List<TraceServiceModel> temp = new ArrayList<>(SERVICES_INFO_LIST);

                temp.stream()
                        //只筛选出trace_services_info表已配置服务码的记录进行更新
                        .filter(traceServiceModel -> DBUtils.selectList(QRY_ALL_SERVICE_BEAN, TraceServiceUpdatePO.createTsiProvider(APP_NAME))
                                                        .stream()
                                                        .anyMatch(serviceName -> traceServiceModel.getBeanName().equals(serviceName)))
                        .forEach(traceServiceModel -> {
                            //添加发布者信息
                            poList.add(TraceServiceUpdatePO.createUpdateProvider(traceServiceModel.getBeanName(),
                                    appName, traceServiceModel.getSpcServiceName(), APP_NAME, traceServiceModel.getPostPath()));
                        });
                if (!CollectionUtils.isEmpty(poList)) {
                    //更新发布者机器信息
                    DBUtils.insert(INSERT_SERVICES_BEAN, TraceServiceUpdateListPO.create(poList));
                }

                SERVICES_INFO_LIST.removeAll(temp);
                temp = null;
            } catch (Exception exception) {
                CommandLog.errorThrow("executeService error!", exception);
            }
        }
    }

    /**
     * 服务发布者应用名@IP
     *
     * @param providerName 服务发布者应用名
     * @return 服务发布者应用名@IP
     */
    private static String buildProviderName(String providerName) {
        return providerName + BusinessConstant.AT_STR + BusinessConstant.VM_HOST;
    }

    /**
     * 获取应用名
     *
     * @return 应用名
     */
    private static String getAppName() {
        String appName = BusinessConstant.EMPTY_STR;
        try {
            appName = AppContextUtils.getForEnv("spring.application.name");
        } catch (Exception ignore) {
        }

        return appName;
    }

    /**
     * 查询消费者,用于更新对应消费者信息,返回多个结果
     *
     * @return k : bean name v : 消费者应用名
     */
    private static Map<String, String> initConsumersMap() {
        Map<String, String> result = new HashMap<>();
        List<TraceServiceBeanAndConsumerPO> beanAndConsumerPos = DBUtils.selectList(QUERY_CONSUMERS);
         boolean hasAppConsumerService = !CollectionUtils.isEmpty(beanAndConsumerPos) &&
                 beanAndConsumerPos.stream().anyMatch(po -> !StringUtils.isEmpty(po.getTsiConsumer()) && po.getTsiConsumer().contains(APP_NAME));
        if (hasAppConsumerService) {
            result = beanAndConsumerPos
                    .stream()
                    .filter(po -> !StringUtils.isEmpty(po.getTsiConsumer()) && po.getTsiConsumer().contains(APP_NAME))
                    .collect(Collectors.toMap(TraceServiceBeanAndConsumerPO::getTsiServiceBeanName, TraceServiceBeanAndConsumerPO::getTsiConsumer));
        }
        return result;
    }

    /**
     * 用于判断是否@RestController注解服务
     *
     * @param beanName    bean name
     * @param servicesCls 接口名
     */
    private static void addServiceInfo(String beanName, String servicesCls, String postPath) {
        SERVICES_INFO_LIST.offer(TraceServiceModel.create(beanName, servicesCls, postPath));
    }

    /**
     * SpringCloud 通过@RestController 进行服务调用，添加相关服务类信息，后续进行服务鉴权
     *
     * @param beanName bean name
     * @param bean     bean实例
     */
    public static void addControllerCls(String beanName, Object bean) {
        Optional.ofNullable(bean.getClass().getAnnotation(RestController.class))
                .ifPresent(restController -> {
                    String postPath = getBeanAllReqPath(bean);
                    if (!StringUtils.isEmpty(postPath)) {
                        TraceService.addServiceInfo(beanName, bean.getClass().getName(), postPath);
                    }
                });
    }

    /**
     * 返回Bean实例所有@RequestMapping(method = RequestMethod.POST)、@PostMapping注解路径
     *
     * @param bean bean实例
     * @return 返回格式: 方法名#Path||方法名#Path   非POST方法返回空字符串
     */
    private static String getBeanAllReqPath(Object bean) {
        String path = BusinessConstant.EMPTY_STR;
        Method[] methods = bean.getClass().getMethods();

        //遍历获取方法下所有@RequestMapping(method = RequestMethod.POST)、@PostMapping注解路径
        String methodsPath = Arrays.stream(methods)
                .filter(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class))
                        && Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                        .anyMatch(requestMethod -> RequestMethod.POST.name().equals(requestMethod.name()))
                        || Objects.nonNull(method.getAnnotation(PostMapping.class)))
                .map(TraceService::buildMethodReqPath)
                .reduce((s1, s2) -> s1 + BusinessConstant.DOUBLE_LINE + s2)
                .orElse(BusinessConstant.EMPTY_STR);

        String classesPath = getReqMappingStr(bean.getClass());

        if (!StringUtils.isEmpty(methodsPath)) {
            path = path.concat(methodsPath);
        }
        if (!StringUtils.isEmpty(classesPath)) {
            if (!StringUtils.isEmpty(path)) {
                path = path.concat(BusinessConstant.LINE);
            }
            path = path.concat(classesPath);
        }

        return path;
    }

    /**
     * 遍历类名下所有的post方法路径
     *
     * @param beanClass 目标类
     * @return 参照com.evy.common.trace.service.TraceService#buildMethodReqPath(java.lang.reflect.Method)
     */
    private static String getReqMappingStr(Class<?> beanClass) {
        String classesPath = BusinessConstant.EMPTY_STR;

        String var = BusinessConstant.EMPTY_STR;
        Class<?>[] classes = beanClass.getInterfaces();
        if (classes.length != 0) {
            var = buildClassesPath(var, getClassesReqPath(classes));
            for (Class<?> aClass : classes) {
                var = buildClassesPath(var, getReqMappingStr(aClass));
            }
        }
        classes = beanClass.getClasses();
        if (classes.length != 0) {
            var = buildClassesPath(var, getClassesReqPath(classes));
            for (Class<?> aClass : classes) {
                var = buildClassesPath(var, getReqMappingStr(aClass));
            }
        }
        if (Objects.nonNull(beanClass.getGenericSuperclass()) && beanClass.getGenericSuperclass() instanceof Class) {
            classes = ((Class) beanClass.getGenericSuperclass()).getClasses();
            if (classes.length != 0) {
                var = buildClassesPath(var, getClassesReqPath(classes));
                for (Class<?> aClass : classes) {
                    var = buildClassesPath(var, getReqMappingStr(aClass));
                }
            }
            classes = ((Class) beanClass.getGenericSuperclass()).getInterfaces();
            if (classes.length != 0) {
                var = buildClassesPath(var, getClassesReqPath(classes));
                for (Class<?> aClass : classes) {
                    var = buildClassesPath(var, getReqMappingStr(aClass));
                }
            }
        }

        classesPath = buildClassesPath(var, classesPath);

        return classesPath;
    }

    private static String getClassesReqPath(Class<?>[] classes) {
        return Arrays.stream(classes)
                .flatMap(clss -> Arrays.stream(clss.getMethods()))
                .filter(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class))
                        && Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                        .anyMatch(requestMethod -> RequestMethod.POST.name().equals(requestMethod.name()))
                        || Objects.nonNull(method.getAnnotation(PostMapping.class)))
                .map(TraceService::buildMethodReqPath)
                .reduce((s1, s2) -> s1 + BusinessConstant.LINE + s2)
                .orElse(BusinessConstant.EMPTY_STR);
    }

    private static String buildClassesPath(String var, String classesPath) {
        if (!classesPath.contains(var)) {
            if (!StringUtils.isEmpty(var)) {
                if (StringUtils.isEmpty(classesPath)) {
                    classesPath = var;
                } else {
                    classesPath = classesPath.concat(BusinessConstant.LINE).concat(var);
                }
            }
        }

        return classesPath;
    }

    /**
     * 构建@RequestMapping(method = RequestMethod.POST)、@PostMapping注解路径字符串<br/>
     * 格式 : 方法名#Path
     *
     * @param method @RequestMapping(method = RequestMethod.POST)、@PostMapping注解方法
     * @return 方法名#Path, 不存在POST方法返回空字符串
     */
    private static String buildMethodReqPath(Method method) {
        CommandLog.info("buildMethodReqPath#method {}", method);
        String[] values;
        String reqPath = "/";
        if (Objects.nonNull(method.getAnnotation(RequestMapping.class))) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            values = requestMapping.value();
        } else {
            values = method.getAnnotation(PostMapping.class).value();
        }

        if (values.length > 0) {
            reqPath = values[0];
        }

        return method.getName().concat(BusinessConstant.SHARE_STR).concat(reqPath);
    }
}