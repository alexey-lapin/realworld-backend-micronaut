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
package com.github.al.realworld.api.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.bus.Command;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * Partial update of the authenticated user. An absent field leaves the current value unchanged.
 */
@Serdeable
public record UpdateUser(
        @Valid @NotNull @Nullable Data user
) implements Command<UpdateUserResult> {

    /**
     * Constraints on the type argument apply to a present field only, so {@code email},
     * {@code username} and {@code password} reject an explicit null while {@code bio} and
     * {@code image} accept one.
     */
    @Serdeable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Data(
            @Nullable JsonNullable<@NotBlank @Email String> email,
            @Nullable JsonNullable<@NotBlank String> username,
            @Nullable JsonNullable<@NotBlank String> password,
            @Nullable JsonNullable<String> image,
            @Nullable JsonNullable<String> bio
    ) {

        public boolean isEmpty() {
            return email == null
                   && username == null
                   && password == null
                   && image == null
                   && bio == null;
        }

    }

}
