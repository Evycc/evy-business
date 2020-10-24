package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.linlin.trace.dto.QryRedisInfoDTO;
import com.evy.linlin.trace.dto.QryRedisInfoOutDTO;
import org.springframework.beans.BeanUtils;

/**
 * 查询Redis服务器健康信息
 * @Author: EvyLiuu
 * @Date: 2020/10/17 22:26
 */
public abstract class QryRedisInfoService extends BaseCommandTemplate<QryRedisInfoDTO, QryRedisInfoOutDTO> implements QryRedisInfo {

    @Override
    public QryRedisInfoOutDTO qryRedisInfoList(QryRedisInfoDTO qryRedisInfoDTO) {
        return convertDto(new QryRedisInfoOutDTO(), start(qryRedisInfoDTO));
    }
}