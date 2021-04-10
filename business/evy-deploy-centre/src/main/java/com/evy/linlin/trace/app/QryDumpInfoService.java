package com.evy.linlin.trace.app;

import com.evy.common.command.app.BaseCommandTemplate;
import com.evy.common.web.HealthyControllerConstant;
import com.evy.linlin.trace.dto.QryDumpInfoDTO;
import com.evy.linlin.trace.dto.QryDumpInfoOutDTO;

/**
 * heap dump及线程实时查询功能
 * @Author: EvyLiuu
 * @Date: 2021/4/5 22:46
 */
public abstract class QryDumpInfoService extends BaseCommandTemplate<QryDumpInfoDTO, QryDumpInfoOutDTO> implements IQryDumpInfo {
    @Override
    public QryDumpInfoOutDTO threadDump(QryDumpInfoDTO dto) {
        dto.setCode(HealthyControllerConstant.THREAD_DUMP_CODE);
        return start(dto);
    }

    @Override
    public QryDumpInfoOutDTO findDeadThreads(QryDumpInfoDTO dto) {
        dto.setCode(HealthyControllerConstant.DEAD_THREAD_DUMP_CODE);
        return start(dto);
    }

    @Override
    public QryDumpInfoOutDTO heapDump(QryDumpInfoDTO dto) {
        dto.setCode(HealthyControllerConstant.HEAP_DUMP_CODE);
        return start(dto);
    }
}
