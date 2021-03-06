package com.evy.linlin.trace.dto;

import com.evy.common.command.app.validator.ValidatorDTO;
import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: EvyLiuu
 * @Date: 2020/10/11 20:01
 */
@Getter
@ToString
public class QryThreadsInfoDTO extends InputDTO implements ValidatorDTO<QryThreadsInfoDTO> {
    /**
     * 编译流水,用于关联编译应用信息
     */
    @NotBlank(message = "buildSeq不能为空")
    @Length(max = 64, message = "buildSeq长度超限")
    private String buildSeq;
    /**
     * 用户标识
     */
    @NotBlank(message = "userSeq不能为空")
    @Length(max = 64, message = "userSeq长度超限")
    private String userSeq;
    /**
     * 查询指定线程时用
     */
    @Length(max = 100, message = "threadName长度超限")
    private String threadName;
    /**
     * 查询指定服务器
     */
    @NotBlank(message = "serviceIp不能为空")
    @Length(max = 100, message = "serviceIp长度超限")
    private String serviceIp;
    /**
     * 忽略的记录数，由于线程信息较多，采取分段返回
     */
    @Min(value = 0, message = "skipIndex最小值0")
    private Integer beginIndex;

    /**
     * 忽略的记录数，由于线程信息较多，采取分段返回
     */
    @Min(value = 100, message = "skipIndex最小值500")
    private Integer endIndex;
}
