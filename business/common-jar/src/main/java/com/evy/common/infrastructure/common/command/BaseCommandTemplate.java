package com.evy.common.infrastructure.common.command;

import com.evy.common.app.validator.ValidatorDTO;
import com.evy.common.domain.repository.factory.MqFactory;
import com.evy.common.domain.repository.mq.MqSender;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.exception.BasicException;
import com.evy.common.infrastructure.common.inceptor.BaseCommandInceptor;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.common.log.anno.TraceLog;
import com.evy.common.infrastructure.tunnel.InputDTO;
import com.evy.common.infrastructure.tunnel.OutDTO;
import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Command模板类
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public abstract class BaseCommandTemplate<T extends InputDTO & ValidatorDTO, R extends OutDTO> implements CommandTemplate<T, R> {
    private final String START_METHOD_NAME = "start";
    /**
     * AnnoCommandInceptor Command拦截器列表
     */
    private static final Map<Class, List<? extends BaseCommandInceptor>> COMMAND_INCEPTORS = new ConcurrentHashMap<>();
    /**
     * 实例对应的拦截器列表
     */
    private List<? extends BaseCommandInceptor> tempInceptor;
    private static final ExecutorService EXECUTOR_SERVICE = MqFactory.returnExecutorService();
//    private static final Gson GSON_FINAL = new Gson();
    /**
     * 记录日志流水topic
     */
    @Getter
    private static final String TRACELOG_TOPIC = "topic-tracelog";
    /**
     * 记录日志流水tag
     */
    @Getter
    private static final String TRACELOG_TAG = "tag-tracelog";
    @Autowired
    @Qualifier("RabbitMqSender")
    private MqSender mqSender;
    /**
     * 当前command上下文
     */
    private Map<String, Object> commandContent = new HashMap<>();
    /**
     * 记录方法执行时长Map
     */
    private Map<String, Long> printTimeMap = new HashMap<>();
    /**
     * 记录方法注释
     */
    private static Map<String, String> methodDescMap = new HashMap<>() {{
        put("init", "init初始化");
        put("before", "before前置方法");
        put("after", "after后置方法");
        put("whenException","封装异常");
    }};

    /**
     * 添加组件注释
     *
     * @param cls  组件类
     * @param desc 组件注释
     */
    public static void addCommand(Class<?> cls, String desc) {
        String className = cls.getName();
        methodDescMap.put(className, desc);
    }

    /**
     * 执行自定义组件，并返回
     * @param t 入参
     * @param function  具体执行逻辑
     * @param <T>   入参类型
     * @param <R>   出参类型
     * @return  返回指定类型出参
     */
    public static <T, R> R executeAssembly(T t, Function<T, R> function) {
        return function.apply(t);
    }

    /**
     * 执行自定义组件
     * @param t 入参
     * @param consumer  具体执行逻辑
     * @param <T>   入参类型
     */
    public static <T> void executeAssembly(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }


    /**
     * 初始化
     */
    private void init() {
        printMethodTakeUpTime(0, null, null);

        if (!CollectionUtils.isEmpty(COMMAND_INCEPTORS)) {
            Class<?> tc = this.getClass();
            if (tempInceptor == null) {
                tempInceptor = COMMAND_INCEPTORS.get(tc);
            }
            if (!CollectionUtils.isEmpty(tempInceptor)) {
                tempInceptor = orderCommandInceptor(tempInceptor);
            }
        }

        printMethodTakeUpTime(1, null, null);
    }

    /**
     * 添加拦截器
     *
     * @param cls       拦截器拦截的类
     * @param inceptors 拦截器列表
     */
    public static void addAllInceptor(Class<?> cls, List<? extends BaseCommandInceptor> inceptors) {
        if (CollectionUtils.isEmpty(COMMAND_INCEPTORS)) {
            COMMAND_INCEPTORS.put(cls, inceptors);
        } else {
            if (COMMAND_INCEPTORS.containsKey(cls)) {
                List value = COMMAND_INCEPTORS.get(cls);
                Collections.addAll(value, inceptors);
            } else {
                COMMAND_INCEPTORS.put(cls, inceptors);
            }
        }
    }

    /**
     * 返回拦截器列表
     *
     * @return 返回拦截器列表
     */
    public static Map returnInceptors() {
        return COMMAND_INCEPTORS;
    }

    /**
     * 模板方法执行
     *
     * @param inputDTO 入参
     * @return 出参
     */
    @RequestMapping(value="/execute", method = RequestMethod.GET)
    @Override
    public R start(T inputDTO) {
        R outDTO = null;
        printMethodTakeUpTime(0, null, null);

        try {
            init();
            before(inputDTO);

            printMethodTakeUpTime(0, this.getClass().getName(), inputDTO);
            outDTO = execute(inputDTO);
            printMethodTakeUpTime(1, this.getClass().getName(), outDTO);
        } catch (Exception e) {
            outDTO = whenException(e, outDTO);
            CommandLog.errorThrow("", e);
        } finally {
            try {
                after(inputDTO);
                //存储到上下文，用于记录@TraceLog
                traceLog(inputDTO, outDTO);
            } catch (Exception e) {
                outDTO = whenException(e, outDTO);
                CommandLog.errorThrow("", e);
            } finally {
                printMethodTakeUpTime(1, null, outDTO);
            }
        }
        return outDTO;
    }

    /**
     * 发送记录日志MQ
     *
     * @param inputDTO 入参
     * @param outDTO   出参
     */
    private void traceLog(T inputDTO, R outDTO) {
        //存储到上下文，用于记录@TraceLog
        Class curClass = this.getClass();
        TraceLog traceLog;
        traceLog = (TraceLog) curClass.getAnnotation(TraceLog.class);
        if (traceLog != null) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    CommandUtils.conveterFromDto(inputDTO, commandContent);
                    CommandUtils.conveterFromDto(outDTO, commandContent);

                    String reqContent = traceLog.reqContent();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String s : reqContent.split(BusinessConstant.SPLIT_LINE)) {
                        if (!StringUtils.isEmpty(s)) {
                            stringBuffer.append(s)
                                    .append(":")
                                    .append(commandContent.get(s))
                                    .append("\t");
                        }
                    }

                    commandContent.put("reqContent", stringBuffer);
                    String curCode = this.getClass().getName();
                    int temp1 = curCode.lastIndexOf(".");
                    curCode = curCode.substring(curCode.substring(0, temp1).lastIndexOf(".") +1, curCode.length() -1);
                    commandContent.put("code", curCode);
                    commandContent.put("serverIp", InetAddress.getLocalHost().getHostAddress());

                    String json = JsonUtils.convertToJson(commandContent);

                    CommandLog.info("Log reqContent: {}", json);
                    mqSender.sendAndConfirm(TRACELOG_TOPIC, TRACELOG_TAG, "", json);
                } catch (UnknownHostException e) {
                    CommandLog.errorThrow("获取本机IP异常", e);
                }
            });
        }
    }

    /**
     * 将异常信息赋值到outdto，封装业务异常，不对外抛出
     *
     * @param e      exception
     * @param outDTO outdto
     */
    private R whenException(Exception e, R outDTO) {
        BasicException be = e instanceof BasicException ? (BasicException) e : new BasicException(e);
        printMethodTakeUpTime(0, null, null);

        String errCode = be.getErrorCode();
        String errMsg = be.getErrorMessage();
        if (outDTO == null) {
            outDTO = (R) new OutDTO();   
        }

        if (errCode != null) {
            outDTO.setErrorCode(be.getErrorCode());   
        }
        if (errMsg != null) {
            outDTO.setErrorMsg(errMsg);   
        }

        printMethodTakeUpTime(1, null, outDTO);

        return outDTO;
    }

    /**
     * 前置拦截器
     *
     * @param t 入参
     * @return outdto
     * @throws BasicException exception
     */
    @Override
    public R before(T t) throws BasicException {
        R outDTO = null;
        printMethodTakeUpTime(0, null, t);

        //校验入参
        Set<ConstraintViolation<T>> violations = t.validator(t);
        if (!CollectionUtils.isEmpty(violations)) {
            for (ConstraintViolation<T> violeation : violations) {
                throw new BasicException("SYS003", violeation.getPropertyPath() + violeation.getMessage());
            }
        }

        if (COMMAND_INCEPTORS.size() > 0 && tempInceptor != null) {
            for (BaseCommandInceptor inceptor : tempInceptor) {
                inceptor.beforeCommand(t);
            }
        }

        printMethodTakeUpTime(1, null, null);
        return outDTO;
    }

    /**
     * 执行后置拦截器
     *
     * @param t inputDTO
     * @return outDTO
     * @throws BasicException exception
     */
    @Override
    public R after(T t) throws BasicException {
        printMethodTakeUpTime(0, null, t);

        if (tempInceptor != null) {
            for (BaseCommandInceptor inceptor : tempInceptor) {
                inceptor.afterCommand(t);
            }
        }

        printMethodTakeUpTime(1, null, null);
        return null;
    }

    /**
     * 打印方法耗时
     *
     * @param se         0 打印start，1 打印end
     * @param methodName method name
     * @param obj        param
     */
    private void printMethodTakeUpTime(int se, String methodName, Object obj) {
        if (BusinessConstant.FAILED != se && BusinessConstant.SUCESS != se) {
            CommandLog.warn("Usage: printMethodTakeUpTime(1) || printMethodTakeUpTime(2)");
        }

        String invokeMethodName = StringUtils.isEmpty(methodName) ?
                new Exception().getStackTrace()[1].getMethodName() : methodName;
        String mapKey = invokeMethodName + Thread.currentThread().getId();
        Long v = printTimeMap.get(mapKey);

        if (BusinessConstant.SUCESS == se) {
            long startTime = v != null ? v : 0L;

            if (startTime == 0L) {
                printTimeMap.put(mapKey, System.currentTimeMillis());
            }
            if (START_METHOD_NAME.equalsIgnoreCase(invokeMethodName)) {
                CommandLog.info("Start Service Flow...", invokeMethodName);
            }
            else {
                if (null != obj) {
                    String param = CommandUtils.returnDtoParam(obj);
                    CommandLog.info("execute {} start. param: {}", invokeMethodName, param);
                } else {
                    CommandLog.info("execute {} start.", invokeMethodName);
                }
            }
        } else {
            long startTime = printTimeMap.get(mapKey);
            printTimeMap.remove(mapKey);
            long endTime = System.currentTimeMillis();
            if (null != obj) {
                String param = CommandUtils.returnDtoParam(obj);
                CommandLog.info("{} return: {}", invokeMethodName, param);
            }
            if (START_METHOD_NAME.equalsIgnoreCase(invokeMethodName)) {
                CommandLog.info("End Service Flow ({})--[{}ms]", methodDescMap.getOrDefault(invokeMethodName, invokeMethodName), (endTime - startTime));   
            }
            else {
                CommandLog.info("execute {} end ({})--[{}ms]", invokeMethodName, methodDescMap.get(invokeMethodName), (endTime - startTime));   
            }
        }
    }

    /**
     * 转换DTO [e >> r]
     * @param r 待转换DTO
     * @param e 源转换DTO
     * @param <E>   一般为与待转换DTO有父子关系的类型
     * @return  返回转换后的DTO对象
     */
    public <E> R convertDto(R r, E e){
        try {
            if (r.getClass().getName().equals(e.getClass().getName())) {
                return (R) e;   
            }

            List<Field> rfieldList;
            List<Field> efieldList;
            Class rtemp = r.getClass();
            Class etemp = e.getClass();

            rfieldList = Arrays.asList(CommandUtils.getAllField(rtemp));
            efieldList = Arrays.asList(CommandUtils.getAllField(etemp));

            for (Field rfield : rfieldList) {
                String toFieldName = rfield.getName();
                String toFieldValue = "";
                for (Field efield : efieldList) {
                    String fromFieldName = efield.getName();
                    if (fromFieldName.equals(toFieldName)){
                        toFieldValue = String.valueOf(CommandUtils.fieldAccessGet(efield, e));
                    }
                }
                CommandUtils.fieldAccessSet(rfield, r, toFieldValue);

            }
        } catch (IllegalAccessException ex) {
            CommandLog.errorThrow("转换DTO异常", ex);
            r = whenException(ex, r);
        }

        return r;
    }

    /**
     * 排序拦截器
     *
     * @param list 拦截器列表
     * @return 排序后的拦截器列表
     */
    private List<BaseCommandInceptor> orderCommandInceptor(List<? extends BaseCommandInceptor> list) {
        return list.stream()
                .sorted(Comparator.comparingInt(BaseCommandInceptor::getOrder))
                .collect(Collectors.toList());
    }
}
