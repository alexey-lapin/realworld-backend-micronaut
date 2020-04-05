package com.github.al.realworld.infrastructure.web;

import com.github.al.bus.Bus;
import com.github.al.realworld.api.command.LoginUser;
import com.github.al.realworld.api.command.LoginUserResult;
import com.github.al.realworld.api.command.RegisterUser;
import com.github.al.realworld.api.command.RegisterUserResult;
import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.command.UpdateUserResult;
import com.github.al.realworld.api.operation.UserOperations;
import com.github.al.realworld.api.query.GetCurrentUser;
import com.github.al.realworld.api.query.GetCurrentUserResult;
import com.github.al.realworld.application.service.AuthenticationService;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller("${api.version}")
public class UserController implements UserOperations {

    private final Bus bus;
    private final AuthenticationService auth;

    @Override
    public LoginUserResult login(@Valid LoginUser command) {
        return bus.executeCommand(command);
    }

    @Override
    public RegisterUserResult register(@Valid RegisterUser command) {
        return bus.executeCommand(command);
    }

    @Override
    public GetCurrentUserResult current() {
        return bus.executeQuery(new GetCurrentUser(auth.currentUsername()));
    }

    @Override
    public UpdateUserResult update(@Valid UpdateUser command) {
        return bus.executeCommand(command);
    }

}
