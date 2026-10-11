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
package com.github.al.realworld.infrastructure.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.token.bearer.BearerTokenReader;
import io.micronaut.security.token.reader.TokenReader;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Reads the token from an {@code Authorization} header using either the {@code Token} scheme the
 * RealWorld spec defines or the standard {@code Bearer} scheme.
 *
 * <p>A malformed header yields no token, so the request is treated as unauthenticated and
 * reported as an invalid token.
 */
@Singleton
@Replaces(BearerTokenReader.class)
public class TokenSchemeTokenReader implements TokenReader<HttpRequest<?>> {

    private static final Pattern PATTERN = Pattern.compile("^(Bearer|Token) (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<String> findToken(HttpRequest<?> request) {
        return request.getHeaders().findFirst(HttpHeaders.AUTHORIZATION)
                .map(PATTERN::matcher)
                .filter(java.util.regex.Matcher::matches)
                .map(matcher -> matcher.group("token"));
    }

}
