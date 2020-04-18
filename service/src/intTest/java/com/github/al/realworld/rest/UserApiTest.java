/*
 * MIT License
 *
 * Copyright (c) 2020 - present Alexey Lapin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.al.realworld.rest;

import com.github.al.realworld.api.command.LoginUser;
import com.github.al.realworld.api.command.RegisterUser;
import com.github.al.realworld.api.dto.UserDto;
import com.github.al.realworld.api.operation.UserClient;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static com.github.al.realworld.rest.auth.AuthSupport.register;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
public class UserApiTest {

    @Inject
    private UserClient userClient;

    @Test
    void should_returnCorrectData_whenRegisterUser() {
        RegisterUser command = registerCommand();

        UserDto user = userClient.register(command).getUser();

        assertThat(user.getUsername()).isEqualTo(command.getUsername());
        assertThat(user.getEmail()).isEqualTo(command.getEmail());
        assertThat(user.getToken()).isNotBlank();
    }

    @Test
    void should_returnCorrectData_whenLoginUser() {
        RegisterUser command = registerCommand();

        userClient.register(command);

        UserDto user = userClient.login(new LoginUser(command.getEmail(), command.getPassword())).getUser();

        assertThat(user.getToken()).isNotBlank();
    }

    @Test
    void should_returnCorrectData_whenLoginUserWithWrongPassword() {
        RegisterUser command = registerCommand();

        userClient.register(command);

        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.login(new LoginUser(command.getEmail(), UUID.randomUUID().toString())),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_return400_whenLoginUser() {
        String s = UUID.randomUUID().toString();
        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.login(new LoginUser(s + "@ex.com", s)),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    void should_returnCorrectData_whenUpdateUser() {
        RegisterUser command = registerCommand();

        userClient.register(command);

        UserDto user = userClient.login(new LoginUser(command.getEmail(), command.getPassword())).getUser();

        assertThat(user.getToken()).isNotBlank();
    }

    @Test
    void name() {
        register().login();

//        UpdateUser updateUser = UpdateUser.builder()
//                .build();
//        userClient.update(updateUser);

    }

    private static RegisterUser registerCommand() {
        return RegisterUser.builder()
                .username(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString() + "@ex.com")
                .password(UUID.randomUUID().toString())
                .build();
    }

}
