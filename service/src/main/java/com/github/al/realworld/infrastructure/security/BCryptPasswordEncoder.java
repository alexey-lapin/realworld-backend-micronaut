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

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.github.al.realworld.application.service.PasswordEncoder;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;

import static com.github.al.realworld.application.exception.BadRequestException.badRequest;

/**
 * Hashes passwords with BCrypt.
 *
 * <p>BCrypt reads at most 72 bytes of input, so a longer password is rejected rather than
 * silently truncated.
 */
@Singleton
public class BCryptPasswordEncoder implements PasswordEncoder {

    private static final int COST = 10;
    private static final int MAX_PASSWORD_BYTES = 72;

    @Override
    public String encode(String rawPassword) {
        var password = rawPassword.getBytes(StandardCharsets.UTF_8);
        if (password.length > MAX_PASSWORD_BYTES) {
            throw badRequest("password", "is too long (maximum is 72 bytes)",
                    "password exceeds %d bytes", MAX_PASSWORD_BYTES);
        }
        return BCrypt.withDefaults().hashToString(COST, rawPassword.toCharArray());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword).verified;
    }

}
