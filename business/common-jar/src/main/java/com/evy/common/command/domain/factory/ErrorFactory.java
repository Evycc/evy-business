package com.evy.common.command.domain.factory;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import com.evy.common.command.infrastructure.tunnel.po.ErrorInfoPO;
import com.evy.common.db.DBUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 错误码工厂，用于加载数据库错误码表及转换错误信息
 *
 * @Author: EvyLiuu
 * @Date: 2020/9/6 0:53
 */
@Component
public class ErrorFactory {
    private Map<String, String> errorCodeMap;
    private final static String QUERY_ALL_ERROR_CODE = "ErrorMapMapper.queryAllErrorCode";

    /**
     * 从数据库加载错误码表，并缓存到内存
     */
    private void loadErrorMap() {
        List<ErrorInfoPO> errorInfoPos = DBUtils.selectList(QUERY_ALL_ERROR_CODE);
        if (!CollectionUtils.isEmpty(errorInfoPos)) {
            errorCodeMap = errorInfoPos.stream()
                    .collect(Collectors.toMap(ErrorInfoPO::getErrorCode, ErrorInfoPO::getErrorMsg));
        }
    }

    /**
     * 根据错误码表,获取对应错误描述
     * @param outDto com.evy.common.command.infrastructure.tunnel.dto.OutDTO
     * @param <T>   outDto子类
     */
    public <T extends OutDTO> void handleErrorCode(T outDto) {
        if (CollectionUtils.isEmpty(errorCodeMap)) {
            loadErrorMap();
        }
        String msg = errorCodeMap.get(outDto.getErrorCode());
        if (!StringUtils.isEmpty(msg)) {
            outDto.setErrorMsg(msg);
        } else {
            outDto.setErrorMsg("系统繁忙");
        }
    }
}
