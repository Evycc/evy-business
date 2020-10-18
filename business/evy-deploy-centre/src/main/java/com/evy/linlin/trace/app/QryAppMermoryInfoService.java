package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryAppMermoryInfoDTO;
import com.evy.linlin.trace.dto.QryAppMermoryInfoOutDTO;
import org.springframework.beans.BeanUtils;

/**
 * 查询服务器内存使用信息
 * @Author: EvyLiuu
 * @Date: 2020/10/11 21:07
 */
public abstract class QryAppMermoryInfoService extends BaseCommandTemplate<QryAppMermoryInfoDTO, QryAppMermoryInfoOutDTO> implements QryAppMermoryInfo {

    @Override
    public QryAppMermoryInfoOutDTO qryAppMemoryList(QryAppMermoryInfoDTO qryAppMermoryInfoDTO) {
        QryAppMermoryInfoOutDTO qryAppMermoryInfoOutDTO = new QryAppMermoryInfoOutDTO();
        BeanUtils.copyProperties(start(qryAppMermoryInfoDTO), qryAppMermoryInfoOutDTO);
        return qryAppMermoryInfoOutDTO;
    }
}
