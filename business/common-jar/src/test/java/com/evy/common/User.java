package com.evy.common;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class User extends InputDTO {
    @NotNull(message = "Name cannot be null")
    private String name;

    @AssertTrue
    private boolean working;

    @Size(min = 10, max = 200, message = "About Me must be between 10 and 200 characters")
    private String aboutMe;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;

    @Email(message = "Email should be valid")
    private String email;

    private String date;
}
