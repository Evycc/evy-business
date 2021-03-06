package com.evy.linlin.trace.dto;

import com.evy.common.command.infrastructure.tunnel.dto.CommandModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 18:04
 */
@AllArgsConstructor
@Getter
public class QryHttpInfoModel extends CommandModel {
    private String appIp;
    private String reqUrl;
    private String takeUpTime;
    private String reqTimeStamp;
    private String reqSuccess;
    private String reqInput;
    private String reqResult;
    private String gmtModify;
}
