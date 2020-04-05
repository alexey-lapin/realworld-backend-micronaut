package com.github.al.realworld.api.operation;

import com.github.al.realworld.api.command.LoginUser;
import com.github.al.realworld.api.command.LoginUserResult;
import com.github.al.realworld.api.command.RegisterUser;
import com.github.al.realworld.api.command.RegisterUserResult;
import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.command.UpdateUserResult;
import com.github.al.realworld.api.query.GetCurrentUserResult;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

import javax.validation.Valid;

public interface UserOperations {

    @Post("/users/login")
    LoginUserResult login(@Valid @Body LoginUser command);

    @Post("/users")
    RegisterUserResult register(@Valid @Body RegisterUser command);

    @Get("/user")
    GetCurrentUserResult current();

    @Put("/user")
    UpdateUserResult update(@Valid @Body UpdateUser command);

}
