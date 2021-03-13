package com.evy.linlin.trace.app;

import com.evy.linlin.trace.dto.QrySlowSqlInfoDTO;
import com.evy.linlin.trace.dto.QrySlowSqlInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询慢SQL、及优化建议
 * @Author: EvyLiuu
 * @Date: 2020/10/7 22:41
 */
@RequestMapping
public interface IQrySlowSqlInfo {
    @PostMapping("/qrySlowSqlInfoList")
    QrySlowSqlInfoOutDTO qrySlowSqlInfoList(@RequestBody QrySlowSqlInfoDTO qrySlowSqlInfoDTO);
}
