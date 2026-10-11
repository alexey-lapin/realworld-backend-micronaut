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
import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.api.dto.UserDto;
import com.github.al.realworld.api.operation.UserClient;
import com.github.al.realworld.rest.auth.AuthSupport;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.al.realworld.rest.auth.AuthSupport.register;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
public class UserApiTest {

    public static final String ALTERED_EMAIL = "altered-email@example.com";
    public static final String ALTERED_USERNAME = "altered-username";
    public static final String ALTERED_PASSWORD = "altered-password";
    public static final String ALTERED_BIO = "altered-bio";
    public static final String ALTERED_IMAGE = "altered-image";

    @Inject
    private UserClient userClient;

    @Test
    void should_returnCorrectData_whenRegisterUser() {
        RegisterUser command = registerCommand();

        UserDto user = userClient.register(command).user();

        assertThat(user.username()).isEqualTo(command.user().username());
        assertThat(user.email()).isEqualTo(command.user().email());
        assertThat(user.token()).isNotBlank();
    }

    @Test
    void should_returnCorrectData_whenLoginUser() {
        RegisterUser command = registerCommand();

        userClient.register(command);

        var data = new LoginUser.Data(command.user().email(), command.user().password());
        UserDto user = userClient.login(new LoginUser(data)).user();

        assertThat(user.token()).isNotBlank();
    }

    @Test
    void should_returnCorrectData_whenLoginUserWithWrongPassword() {
        RegisterUser command = registerCommand();

        userClient.register(command);

        var data = new LoginUser.Data(command.user().email(), UUID.randomUUID().toString());
        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.login(new LoginUser(data)),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_throw401_when_loginUserDoesNotExist() {
        String s = UUID.randomUUID().toString();
        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.login(new LoginUser(new LoginUser.Data(s + "@ex.com", s))),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_returnCorrectData_whenUpdateUser() {
        register().login();

        var data = new UpdateUser.Data(
                new JsonNullable<>(ALTERED_EMAIL),
                new JsonNullable<>(ALTERED_USERNAME),
                new JsonNullable<>(ALTERED_PASSWORD),
                new JsonNullable<>(ALTERED_IMAGE),
                new JsonNullable<>(ALTERED_BIO));

        UserDto user = userClient.update(new UpdateUser(data)).user();

        assertThat(user.email()).isEqualTo(ALTERED_EMAIL);
        assertThat(user.username()).isEqualTo(ALTERED_USERNAME);
        assertThat(user.bio()).isEqualTo(ALTERED_BIO);
        assertThat(user.image()).isEqualTo(ALTERED_IMAGE);
    }

    @Test
    void should_throw409_when_updateUserWithExistingEmail() {
        AuthSupport.RegisteredUser registeredUser = register();

        register().login();

        var data = new UpdateUser.Data(new JsonNullable<>(registeredUser.email()), null, null, null, null);

        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.update(new UpdateUser(data)),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.CONFLICT.getCode());
    }

    @Test
    void should_throw409_when_updateUserWithExistingName() {
        AuthSupport.RegisteredUser registeredUser = register();

        register().login();

        var data = new UpdateUser.Data(null, new JsonNullable<>(registeredUser.username()), null, null, null);

        HttpClientResponseException exception = catchThrowableOfType(
                () -> userClient.update(new UpdateUser(data)),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.CONFLICT.getCode());
    }

    private static RegisterUser registerCommand() {
        var data = new RegisterUser.Data(
                UUID.randomUUID() + "@ex.com",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        return new RegisterUser(data);
    }

}
