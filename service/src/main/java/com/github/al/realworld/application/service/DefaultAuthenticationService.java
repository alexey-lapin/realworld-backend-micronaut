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

import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import static com.github.al.realworld.application.exception.UnauthorizedException.invalidToken;

@RequiredArgsConstructor
@Singleton
public class DefaultAuthenticationService implements AuthenticationService {

    private final SecurityService securityService;

    /**
     * Returns the id from the {@code uid} claim, rejecting an authenticated token whose claim is
     * missing or not a number.
     */
    @Override
    public @Nullable Long getCurrentUserId() {
        var authentication = securityService.getAuthentication().orElse(null);
        if (authentication == null) {
            return null;
        }
        var uid = authentication.getAttributes().get(JwtService.CLAIM_UID);
        if (uid instanceof Number number) {
            return number.longValue();
        }
        throw invalidToken("token has no numeric %s claim", JwtService.CLAIM_UID);
    }

    @Override
    public @Nullable String getCurrentUserName() {
        return securityService.username().orElse(null);
    }

}
