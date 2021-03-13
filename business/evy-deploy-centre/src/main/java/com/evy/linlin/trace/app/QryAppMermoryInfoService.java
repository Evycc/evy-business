package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryAppMermoryInfoDTO;
import com.evy.linlin.trace.dto.QryAppMermoryInfoOutDTO;

/**
 * 查询服务器内存使用信息
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:07
 */
public abstract class QryAppMermoryInfoService extends BaseCommandTemplate<QryAppMermoryInfoDTO, QryAppMermoryInfoOutDTO> implements IQryAppMermoryInfo {

    @Override
    public QryAppMermoryInfoOutDTO qryAppMemoryList(QryAppMermoryInfoDTO qryAppMermoryInfoDTO) {
        return start(qryAppMermoryInfoDTO);
    }
}
