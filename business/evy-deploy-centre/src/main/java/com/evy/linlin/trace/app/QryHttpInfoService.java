package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryHttpInfoDTO;
import com.evy.linlin.trace.dto.QryHttpInfoOutDTO;

/**
 * Http请求耗时及响应信息收集
 * @Author: EvyLiuu
 * @Date: 2020/10/17 21:29
 */
public abstract class QryHttpInfoService extends BaseCommandTemplate<QryHttpInfoDTO, QryHttpInfoOutDTO> implements IQryHttpInfo {

    @Override
    public QryHttpInfoOutDTO qryHttpInfoList(QryHttpInfoDTO qryHttpInfoDTO) {
        return start(qryHttpInfoDTO);
    }
}
