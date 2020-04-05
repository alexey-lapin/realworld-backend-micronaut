package com.github.al.realworld.api.command;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.github.al.bus.Command;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
@Introspected
@JsonRootName("user")
public class UpdateUser implements Command<UpdateUserResult> {

    @Email
    private String email;
    private String username;
    private String password;
    private String image;
    private String bio;

}
