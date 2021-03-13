package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryServiceInfoDTO;
import com.evy.linlin.trace.dto.QryServiceInfoOutDTO;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/18 0:03
 */
public abstract class QryServiceInfoService extends BaseCommandTemplate<QryServiceInfoDTO, QryServiceInfoOutDTO> implements IQryServiceInfo {

    @Override
    public QryServiceInfoOutDTO qryServiceInfoList(QryServiceInfoDTO qryServiceInfoDTO) {
        return start(qryServiceInfoDTO);
    }
}
