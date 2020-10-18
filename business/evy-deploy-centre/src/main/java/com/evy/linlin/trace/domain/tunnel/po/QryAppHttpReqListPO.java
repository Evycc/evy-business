package com.evy.linlin.trace.domain.tunnel.po;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/17 14:44
 */
@Getter
@ToString
public class QryAppHttpReqListPO {
    private String thfReqIp;
    private String thfUrl;
    private String thfTakeupTime;
    private String thfReqTimestamp;
    private String thfResultSucess;
    private String thfInput;
    private String thfResult;
    private String gmtModify;
}
