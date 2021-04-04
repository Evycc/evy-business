package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 12:00
 */
@Getter
@ToString
@NoArgsConstructor
@Setter
public class QryAppMermoryInfoOutDTO extends OutDTO {
    private Map<String, List<QryAppMermoryInfoModel>> outMap;
}
