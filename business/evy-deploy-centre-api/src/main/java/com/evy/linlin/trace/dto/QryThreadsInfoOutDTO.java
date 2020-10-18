package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:02
 */
@Getter
@ToString
@Setter
@NoArgsConstructor
public class QryThreadsInfoOutDTO extends OutDTO {
    private List<QryThreadsInfoModel> list;
}
