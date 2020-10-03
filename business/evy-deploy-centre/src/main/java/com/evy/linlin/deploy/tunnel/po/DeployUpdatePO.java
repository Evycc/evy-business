package com.evy.linlin.deploy.tunnel.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: EvyLiuu
 * @Date: 2020/9/26 17:26
 */
@ToString
@Getter
@AllArgsConstructor
public class DeployUpdatePO {
    private String jarPath;
    private String stage;
    private String seq;
    private String buildLog;
}
