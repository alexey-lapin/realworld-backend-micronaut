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

import com.github.al.realworld.api.dto.ProfileDto;
import com.github.al.realworld.api.operation.ProfileClient;
import com.github.al.realworld.api.query.GetProfileResult;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.github.al.realworld.rest.auth.AuthSupport.logout;
import static com.github.al.realworld.rest.auth.AuthSupport.register;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
public class ProfileApiTest {

    @Inject
    private ProfileClient profileClient;

    @AfterEach
    void afterEach() {
        logout();
    }

    @Test
    void should_returnNull_when_userIsNotRegistered() {
        GetProfileResult result = profileClient.findByUsername("u3");

        assertThat(result).isNull();
    }

    @Test
    void should_returnCorrectData_when_userIsRegistered() {
        register("u1", "u1@example.com", "1234");

        ProfileDto profile = profileClient.findByUsername("u1").getProfile();

        assertThat(profile.getUsername()).isEqualTo("u1");
    }

    @Test
    void should_throw401_when_unauthorized() {
        HttpClientResponseException ex = catchThrowableOfType(
                () -> profileClient.follow("u2"),
                HttpClientResponseException.class
        );

        assertThat(ex.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_returnCorrectProfileData_when_followAndUnfollow() {
        register().login();
        String user2 = register().getUsername();

        profileClient.follow(user2);

        ProfileDto profile1 = profileClient.findByUsername(user2).getProfile();
        assertThat(profile1.getFollowing()).isTrue();

        profileClient.unfollow(user2);

        ProfileDto profile2 = profileClient.findByUsername(user2).getProfile();
        assertThat(profile2.getFollowing()).isFalse();
    }

}
