package com.evy.common.command.domain.factory;

import com.evy.common.command.infrastructure.constant.BeanNameConstant;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.command.infrastructure.tunnel.po.ErrorInfoPO;
import com.evy.common.database.DBUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 错误码工厂，用于加载数据库错误码表及转换错误信息
 *
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:53
 */
@Component
@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
public class ErrorFactory {
    private static Map<String, String> ERROR_MAP;
    private final static String QUERY_ALL_ERROR_CODE = "ErrorMapMapper.queryAllErrorCode";
    private static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE = CreateFactory.returnScheduledExecutorService("ErrorMap-ThreadTask", 1);

    {
        loadErrorMap();
    }

    /**
     * 从数据库加载错误码表，并缓存到内存
     */
    private void loadErrorMap() {
        //定时监控队列，存在数据则进行处理后入库
        //1分钟后执行
        long initialDelay = 0L;
        //间隔10分钟轮询
        long delay = 600000L;
        EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            List<ErrorInfoPO> errorInfoPos = DBUtils.selectList(QUERY_ALL_ERROR_CODE);
            if (!CollectionUtils.isEmpty(errorInfoPos)) {
                ERROR_MAP = errorInfoPos.stream()
                        .collect(Collectors.toMap(ErrorInfoPO::getErrorCode, ErrorInfoPO::getErrorMsg));
            }
        }, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 根据错误码表,获取对应错误描述
     * @param outDto com.evy.common.command.infrastructure.tunnel.dto.OutDTO
     * @param <T>   outDto子类
     */
    public <T extends OutDTO> void handleErrorCode(T outDto) {
        if (CollectionUtils.isEmpty(ERROR_MAP)) {
            loadErrorMap();
        }
        String msg = ERROR_MAP.get(outDto.getErrorCode());
        if (!StringUtils.isEmpty(msg)) {
            outDto.setErrorMsg(msg);
        } else {
            outDto.setErrorMsg("系统繁忙");
        }
    }

    public static void handleErrorCode(BasicException basicException) {
        String msg = ERROR_MAP.get(basicException.getErrorCode());
        if (!StringUtils.isEmpty(msg)) {
            basicException.setErrorMessage(msg);
        } else {
            basicException.setErrorMessage("未知错误码");
        }
    }
}
