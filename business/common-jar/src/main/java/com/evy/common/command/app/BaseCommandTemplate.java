package com.evy.common.command.app;

import com.evy.common.command.app.inceptor.BaseCommandInceptor;
import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.log.CommandLog;
import com.evy.common.log.infrastructure.tunnel.anno.TraceLog;
import com.evy.common.mq.common.app.basic.MqSender;
import com.evy.common.utils.CommandUtils;
import com.evy.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Command模板类
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public abstract class BaseCommandTemplate<T extends InputDTO & ValidatorDTO, R extends OutDTO> implements CommandTemplate<T, R> {
    /**
     * AnnoCommandInceptor Command拦截器列表
     */
    private static final Map<Class<?>, List<? extends BaseCommandInceptor>> COMMAND_INCEPTORS = new ConcurrentHashMap<>();
    /**
     * 实例对应的拦截器列表
     */
    private List<? extends BaseCommandInceptor> tempInceptor;

    /**
     * 记录日志流水topic
     */
    public static final String TRACELOG_TOPIC = "topic-tracelog";
    /**
     * 记录日志流水tag
     */
    public static final String TRACELOG_TAG = "tag-tracelog";
    @Autowired
    @Qualifier("RabbitMqSender")
    private MqSender mqSender;
    /**
     * 当前command上下文
     */
    private final Map<String, Object> commandContent = new HashMap<>();

    /**
     * 初始化
     */
    private void init() {
        if (!CollectionUtils.isEmpty(COMMAND_INCEPTORS)) {
            Class<?> tc = this.getClass();
            if (tempInceptor == null) {
                tempInceptor = COMMAND_INCEPTORS.get(tc);
            }
            if (!CollectionUtils.isEmpty(tempInceptor)) {
                tempInceptor = orderCommandInceptor(tempInceptor);
            }
        }
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
    @Override
    public R start(T inputDTO) {
        R outDTO = null;

        try {
            init();
            before(inputDTO);

            outDTO = execute(inputDTO);
        } catch (Exception e) {
            outDTO = whenException(e, outDTO);
            CommandLog.errorThrow("", e);
        } finally {
            try {
                after(inputDTO);
            } catch (Exception e) {
                outDTO = whenException(e, outDTO);
                CommandLog.errorThrow("", e);
            } finally {
                //存储到上下文，用于记录@TraceLog
                traceLog(inputDTO, outDTO);
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
            int temp1 = curCode.lastIndexOf(BusinessConstant.POINT);
            curCode = curCode.substring(curCode.substring(0, temp1).lastIndexOf(BusinessConstant.POINT) +1, curCode.length() -1);
            commandContent.put("code", curCode);
            commandContent.put("serverIp", BusinessConstant.VM_HOST);

            String json = JsonUtils.convertToJson(commandContent);

            CommandLog.info("Log reqContent: {}", json);
            mqSender.sendAndConfirm(TRACELOG_TOPIC, TRACELOG_TAG, BusinessConstant.EMPTY_STR, json);
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
        if (tempInceptor != null) {
            for (BaseCommandInceptor inceptor : tempInceptor) {
                inceptor.afterCommand(t);
            }
        }
        return null;
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
            if (r.getClass().equals(e.getClass())) {
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
