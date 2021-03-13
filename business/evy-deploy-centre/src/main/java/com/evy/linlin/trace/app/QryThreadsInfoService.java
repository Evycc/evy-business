package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.linlin.trace.dto.QryThreadsInfoDTO;
import com.evy.linlin.trace.dto.QryThreadsInfoOutDTO;

/**
 * 查询线程信息,清晰展示线程阻塞、死锁情况
 * @Author: EvyLiuu
 * @Date: 2020/10/18 13:56
 */
public abstract class QryThreadsInfoService extends BaseCommandTemplate<QryThreadsInfoDTO, QryThreadsInfoOutDTO> implements IQryThreadsInfo {

    @Override
    public QryThreadsInfoOutDTO qryThreadsInfo(QryThreadsInfoDTO qryThreadsInfoDto) {
        return start(qryThreadsInfoDto);
    }
}
