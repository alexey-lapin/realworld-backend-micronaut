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

import com.github.al.realworld.rest.auth.AuthSupport;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * Pins the error envelope for failures raised outside the handlers: validation, security
 * rejections and unreadable bodies.
 */
@MicronautTest
class ErrorResponseApiTest {

    @Inject
    @Client("/api")
    private HttpClient client;

    @Test
    void should_return422WithFieldErrors_when_registerPayloadIsBlank() {
        var response = exchangeError(HttpRequest.POST("/users", """
                {"user":{"username":"","email":"not-an-email","password":""}}"""));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        assertThat(errors(response)).containsEntry("username", List.of("can't be blank"))
                .containsEntry("email", List.of("is invalid"))
                .containsEntry("password", List.of("can't be blank"));
    }

    @Test
    void should_reportBlankBeforeInvalid_when_emailIsEmpty() {
        var response = exchangeError(HttpRequest.POST("/users/login", """
                {"user":{"email":"","password":"secret"}}"""));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        assertThat(errors(response).get("email")).first().isEqualTo("can't be blank");
    }

    @Test
    void should_return401TokenMissing_when_noAuthorizationHeader() {
        var response = exchangeError(HttpRequest.GET("/user"));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        assertThat(errors(response)).isEqualTo(Map.of("token", List.of("is missing")));
    }

    @Test
    void should_return401TokenInvalid_when_tokenIsNotValid() {
        var response = exchangeError(HttpRequest.GET("/user").header("Authorization", "Token not-a-jwt"));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        assertThat(errors(response)).isEqualTo(Map.of("token", List.of("is invalid")));
    }

    @Test
    void should_serveAnonymously_when_tokenIsInvalidOnPublicRoute() {
        var response = client.toBlocking()
                .exchange(HttpRequest.GET("/tags").header("Authorization", "Token not-a-jwt"), Map.class);

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.OK.getCode());
    }

    @Test
    void should_return401CredentialsInvalid_when_passwordIsOverlong() {
        var user = AuthSupport.register();
        for (var password : List.of("a".repeat(73), "é".repeat(40))) {
            var response = exchangeError(HttpRequest.POST("/users/login", """
                    {"user":{"email":"%s","password":"%s"}}""".formatted(user.email(), password)));

            assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            assertThat(errors(response)).isEqualTo(Map.of("credentials", List.of("invalid")));
        }
    }

    @Test
    void should_keepProtocolStatus_when_methodIsNotAllowed() {
        // authenticated, since security rejects anonymous calls before routing
        AuthSupport.register().login();
        var response = exchangeError(HttpRequest.PATCH("/tags", "{}"));
        AuthSupport.logout();

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.getCode());
    }

    @Test
    void should_return401CredentialsInvalid_when_loginFails() {
        var response = exchangeError(HttpRequest.POST("/users/login", """
                {"user":{"email":"%s@ex.com","password":"secret"}}""".formatted(UUID.randomUUID())));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        assertThat(errors(response)).isEqualTo(Map.of("credentials", List.of("invalid")));
    }

    @Test
    void should_return404ArticleNotFound_when_slugDoesNotExist() {
        var response = exchangeError(HttpRequest.GET("/articles/" + UUID.randomUUID()));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        assertThat(errors(response)).isEqualTo(Map.of("article", List.of("not found")));
    }

    @Test
    void should_return422_when_bodyIsNotJson() {
        var response = exchangeError(HttpRequest.POST("/users", "{not json").contentType("application/json"));

        assertThat(response.getStatus().getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        assertThat(errors(response)).containsKey("body");
    }

    private HttpClientResponseException exchangeError(HttpRequest<?> request) {
        return catchThrowableOfType(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<String>> errors(HttpClientResponseException exception) {
        Map<String, Object> body = exception.getResponse().getBody(Map.class).orElseThrow();
        return (Map<String, List<String>>) body.get("errors");
    }

}
