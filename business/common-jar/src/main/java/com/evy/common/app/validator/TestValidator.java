package com.evy.common.app.validator;

import com.evy.common.infrastructure.tunnel.InputDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class TestValidator {
    private  static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validator(InputDTO t){
        if (t != null){
            //dto存在校验失败的信息，会被存到Set中
            Set<ConstraintViolation<InputDTO>> violations = validator.validate(t);
            if (violations.size() > 0){
                violations.stream().forEach(err -> {
                    if (err != null){
                        System.out.println(err.getMessage());
                        System.out.println(err);
                    }
                });
            }
        }
    }
}
