package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:08
 */
@Getter
@ToString
@NoArgsConstructor
@Setter
public class QryMqTraceInfoOutDTO extends OutDTO {
    private List<QryMqTraceInfoModel> list;
}
