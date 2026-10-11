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
package com.github.al.realworld.application.service;

import com.github.al.realworld.domain.model.User;
import io.micronaut.security.authentication.ServerAuthentication;
import io.micronaut.security.token.generator.AccessRefreshTokenGenerator;
import io.micronaut.security.token.render.AccessRefreshToken;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Issues tokens whose subject is the username, with the user id in the {@code uid} claim.
 */
@RequiredArgsConstructor
@Singleton
public class DefaultJwtService implements JwtService {

    private final AccessRefreshTokenGenerator tokenGenerator;

    @Override
    public String getToken(User user) {
        var authentication = new ServerAuthentication(user.getUsername(),
                List.of(),
                Map.of(CLAIM_UID, user.getId().toString()));
        return tokenGenerator.generate(authentication)
                .map(AccessRefreshToken::getAccessToken)
                .orElseThrow(() -> new IllegalStateException("token generation failed"));
    }

}
