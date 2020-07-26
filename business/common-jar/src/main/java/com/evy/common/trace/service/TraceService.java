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
import java.util.stream.Stream;

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
    private static final Map<String, String> SERVICES_MAP = new HashMap<>(16);
    private static Map<String, String> SERVICES_TEMP_MAP = new HashMap<>(16);
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
                DBUtils.update(UPDATE_CLEAN_IP, TraceServiceUpdatePO.createUpdateConsumer(ip, ip));
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
                        SERVICES_TEMP_MAP = initConsumersMap();
                        if (!CollectionUtils.isEmpty(SERVICES_TEMP_MAP)) {
                            SERVICES_MAP.clear();
                            SERVICES_MAP.putAll(SERVICES_TEMP_MAP);
                            SERVICES_TEMP_MAP = null;
                        }
                    }
                });
        if (!CollectionUtils.isEmpty(SERVICES_INFO_LIST)) {
            try {
                List<TraceServiceModel> temp = new ArrayList<>(SERVICES_INFO_LIST);

                List<TraceServiceUpdatePO> poList = new ArrayList<>(temp.size());
                List<TraceServiceUpdatePO> cList = new ArrayList<>(temp.size());

                temp.forEach(traceServiceModel -> {
                    String beanName = traceServiceModel.getBeanName();
                    String appName = buildProviderName(APP_NAME);
                    //添加消费者信息
                    if (!CollectionUtils.isEmpty(SERVICES_MAP) && SERVICES_MAP.containsValue(APP_NAME)) {
                        cList.add(TraceServiceUpdatePO.createUpdateConsumer(beanName, appName));
                    }

                    //添加发布者信息
                    poList.add(TraceServiceUpdatePO.createUpdateProvider(traceServiceModel.getBeanName(),
                            appName, traceServiceModel.getSpcServiceName(), APP_NAME, traceServiceModel.getPostPath()));
                });

                if (!CollectionUtils.isEmpty(cList)) {
                    //更新消费者机器信息
                    if (cList.size() > BusinessConstant.FAILED) {
                        //大于1,进行批量update
                        DBUtils.batchAny(cList.stream()
                                .map(updatePo -> DBUtils.BatchModel.create(UPDATE_SERVICES_CONSUMER, updatePo, DBUtils.BatchType.UPDATE))
                                .collect(Collectors.toList())
                        );
                    } else {
                        cList.forEach(updatePo -> DBUtils.update(UPDATE_SERVICES_CONSUMER, updatePo));
                    }
                }
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
        List<TraceServiceBeanAndConsumerPO> beanAndConsumerPos = DBUtils.selectList(QUERY_CONSUMERS);
        if (!CollectionUtils.isEmpty(beanAndConsumerPos)) {
            return beanAndConsumerPos.stream()
                    .filter(po -> po.getTsiConsumer().equals(APP_NAME))
                    .collect(Collectors.toMap(TraceServiceBeanAndConsumerPO::getTsiServiceBeanName, TraceServiceBeanAndConsumerPO::getTsiConsumer));
        }
        return null;
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
        Class<?>[] classes = bean.getClass().getInterfaces();

        //遍历获取方法下所有@RequestMapping(method = RequestMethod.POST)、@PostMapping注解路径
        String methodsPath = Arrays.stream(methods)
                .filter(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class))
                        && Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                        .anyMatch(requestMethod -> RequestMethod.POST.name().equals(requestMethod.name()))
                        || Objects.nonNull(method.getAnnotation(PostMapping.class)))
                .map(TraceService::buildMethodReqPath)
                .reduce((s1, s2) -> s1 + BusinessConstant.DOUBLE_LINE + s2)
                .orElse(BusinessConstant.EMPTY_STR);

        //遍历获取接口下所有@RequestMapping(method = RequestMethod.POST)、@PostMapping注解路径
        String classesPath = Arrays.stream(classes)
                .flatMap(clss -> Arrays.stream(clss.getMethods()))
                .filter(method -> Objects.nonNull(method.getAnnotation(RequestMapping.class))
                        && Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                        .anyMatch(requestMethod -> RequestMethod.POST.name().equals(requestMethod.name()))
                        || Objects.nonNull(method.getAnnotation(PostMapping.class)))
                .map(TraceService::buildMethodReqPath)
                .reduce((s1, s2) -> s1 + BusinessConstant.DOUBLE_LINE + s2)
                .orElse(BusinessConstant.EMPTY_STR);

        if (!StringUtils.isEmpty(methodsPath)) {
            path = path.concat(methodsPath);
        }
        if (!StringUtils.isEmpty(classesPath)) {
            path = path.concat(classesPath);
        }

        return path;
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
        if (Objects.nonNull(method.getAnnotation(RequestMapping.class))) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            return method.getName().concat(BusinessConstant.SHARE_STR).concat(requestMapping.value()[0]);
        }

        return method.getName().concat(BusinessConstant.SHARE_STR).concat(method.getAnnotation(PostMapping.class).value()[0]);
    }
}