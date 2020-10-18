package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.command.infrastructure.exception.BasicException;
import com.evy.linlin.trace.dto.QrySlowSqlInfoDTO;
import com.evy.linlin.trace.dto.QrySlowSqlInfoOutDTO;

/**
 * 查询慢SQL、及优化建议
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:24
 */
public abstract class QrySlowSqlInfoService extends BaseCommandTemplate<QrySlowSqlInfoDTO, QrySlowSqlInfoOutDTO> implements QrySlowSqlInfo {

    @Override
    public QrySlowSqlInfoOutDTO qrySlowSqlInfoList(QrySlowSqlInfoDTO qrySlowSqlInfoDTO) {
        return convertDto(new QrySlowSqlInfoOutDTO(), start(qrySlowSqlInfoDTO));
    }
}
