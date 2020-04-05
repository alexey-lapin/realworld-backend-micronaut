package com.github.al.realworld.api.command;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.github.al.bus.Command;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Introspected
@Getter
@JsonRootName("user")
public class LoginUser implements Command<LoginUserResult> {

    @Email
    private String email;
    @NotBlank
    private String password;

}
