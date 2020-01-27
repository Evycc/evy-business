package com.evy.common.app.validator;

import com.evy.common.infrastructure.tunnel.InputDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO字段校验
 * @Author: EvyLiuu
 * @Date: 2019/10/23 0:12
 */
public interface ValidatorDTO<T extends InputDTO> {
    Validator VALIDATOR_FINAL = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 校验DTO默认方法
     * @param t inputDTO
     * @return  校验字段异常集合
     */
    default Set<ConstraintViolation<T>> validator(T t) {
        Set<ConstraintViolation<T>> violations = new HashSet<>(3);
        if (t != null){
            //dto存在校验失败的信息，会被存到Set中
            violations = VALIDATOR_FINAL.validate(t);
        }
        return violations;
    }
}
