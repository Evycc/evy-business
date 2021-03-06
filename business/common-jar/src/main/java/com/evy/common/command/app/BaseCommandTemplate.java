package com.evy.common.command.app;

import com.evy.common.command.app.inceptor.BaseCommandInceptor;
import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.domain.factory.ErrorFactory;
import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.command.infrastructure.constant.ErrorConstant;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Command模板类
 *
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public abstract class BaseCommandTemplate<T extends InputDTO & ValidatorDTO<T>, R extends OutDTO> implements CommandTemplate<T, R> {
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
    @Value("${evy.traceLog.topic:}")
    public String traceLogTopic;
    /**
     * 记录日志流水tag
     */
    @Value("${evy.traceLog.tag:}")
    public String traceLogTag;
    @Autowired
    @Qualifier(BeanNameConstant.RABBIT_MQ_SENDER)
    private MqSender mqSender;
    /**
     * 当前command上下文
     */
    private final Map<String, Object> commandContent = new HashMap<>();
    @Autowired
    private ErrorFactory errorFactory;

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
        } finally {
            try {
                after(inputDTO);
            } catch (Exception e) {
                outDTO = whenException(e, outDTO);
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
        if (StringUtils.isEmpty(traceLogTopic) || StringUtils.isEmpty(traceLogTag)) {
            return;
        }

        //存储到上下文，用于记录@TraceLog
        Class curClass = this.getClass();
        TraceLog traceLog;
        traceLog = (TraceLog) curClass.getAnnotation(TraceLog.class);
        if (traceLog != null) {
            String reqContent = traceLog.reqContent();
            Map<String, Object> map = null;

            if (StringUtils.isEmpty(reqContent)) {
                map = new HashMap<>(2);
                map.put("input", inputDTO);
                map.put("output", outDTO);
            } else {
                String[] reqs = reqContent.split(BusinessConstant.SPLIT_LINE);
                CommandUtils.conveterFromDto(inputDTO, commandContent);
                CommandUtils.conveterFromDto(outDTO, commandContent);
                for (String s : reqs) {
                    map = new HashMap<>(reqs.length);
                    map.put(s, String.valueOf(commandContent.get(s)));
                }
            }

            String reqContentJson = JsonUtils.convertToJson(map);
            if (reqContentJson.length() > 2048) {
                reqContentJson = reqContentJson.substring(0, 2048);
            }

            commandContent.put("reqContent", reqContentJson);
            String curCode = this.getClass().getName();
            int temp1 = curCode.lastIndexOf(BusinessConstant.POINT);
            curCode = curCode.substring(curCode.substring(0, temp1).lastIndexOf(BusinessConstant.POINT) + 1, curCode.length() - 1);
            commandContent.put("code", curCode);
            commandContent.put("serverIp", BusinessConstant.VM_HOST);
            commandContent.put("clientIp", inputDTO.getClientIp());
            commandContent.put("srcSendNo", inputDTO.getSrcSendNo());
            commandContent.put("errorCode", outDTO.getErrorCode());
            commandContent.put("errorMsg", outDTO.getErrorMsg());

            String json = JsonUtils.convertToJson(commandContent);

            CommandLog.info("Log reqContent: {}", json);
            mqSender.sendAndConfirm(traceLogTopic, traceLogTag, BusinessConstant.EMPTY_STR, json);
        }
    }

    /**
     * 将异常信息赋值到outdto，封装业务异常，不对外抛出
     *
     * @param e      exception
     * @param outDTO outdto
     */
    @SuppressWarnings("unchecked")
    private R whenException(Exception e, R outDTO) {
        BasicException be = e instanceof BasicException ? (BasicException) e : new BasicException(e);
        String errCode = be.getErrorCode();
        String errMsg = be.getErrorMessage();
        if (outDTO == null) {
            outDTO = (R) new OutDTO();
        }

        if (!StringUtils.isEmpty(errCode)) {
            outDTO.setErrorCode(be.getErrorCode());
        }
        if (!StringUtils.isEmpty(errMsg)) {
            outDTO.setErrorMsg(errMsg);
        } else {
            errorFactory.handleErrorCode(outDTO);
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
    @SuppressWarnings("unchecked")
    public R before(T t) throws BasicException {
        R outDTO = null;

        if (Objects.nonNull(t)) {
            //校验入参
            Set<ConstraintViolation<T>> violations = t.validator(t);
            if (!CollectionUtils.isEmpty(violations)) {
                for (ConstraintViolation<T> violeation : violations) {
                    throw new BasicException(ErrorConstant.ERROR_VALIDATOR, violeation.getMessage());
                }
            }

            if (COMMAND_INCEPTORS.size() > 0 && tempInceptor != null) {
                for (BaseCommandInceptor inceptor : tempInceptor) {
                    inceptor.beforeCommand(t);
                }
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
    @SuppressWarnings("unchecked")
    public R after(T t) throws BasicException {
        if (tempInceptor != null) {
            for (BaseCommandInceptor inceptor : tempInceptor) {
                inceptor.afterCommand(t);
            }
        }
        return null;
    }

    /**
     * 排序拦截器
     *
     * @param list 拦截器列表
     * @return 排序后的拦截器列表
     */
    @SuppressWarnings("unchecked")
    private List<BaseCommandInceptor> orderCommandInceptor(List<? extends BaseCommandInceptor> list) {
        return list.stream()
                .sorted(Comparator.comparingInt(BaseCommandInceptor::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 强转为指定类型
     * @param source 源对象
     * @param target 目标对象
     * @return 返回source强转后对象，强转失败原样返回target
     */
    @SuppressWarnings("unchecked")
    public R convertOutDto(Object source, R target) {
        try {
            if (!source.getClass().equals(OutDTO.class)) {
                target = (R) source;
            } else {
                target.setErrorCode(((OutDTO)source).getErrorCode());
                target.setErrorMsg(((OutDTO)source).getErrorMsg());
            }
        } catch (Exception exception) {
            CommandLog.errorThrow("DTO转换异常", exception);
        }

        return target;
    }
}
