package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.OutDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:07
 */
@Getter
@ToString
@NoArgsConstructor
@Setter
public class QryHttpInfoOutDTO extends OutDTO {
    private List<QryHttpInfoModel> list;
}
