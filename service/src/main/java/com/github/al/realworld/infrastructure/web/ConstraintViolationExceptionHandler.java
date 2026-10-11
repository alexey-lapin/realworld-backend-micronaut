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
package com.github.al.realworld.infrastructure.web;

import com.github.al.realworld.api.dto.GenericError;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Comparator;
import java.util.Map;

import static java.util.Objects.requireNonNullElse;

/**
 * Renders failed request validation as a 422 {@link GenericError}, keyed by the leaf field of
 * each violation, in place of Micronaut's default validation error response.
 */
@Produces
@Singleton
@Replaces(ConstraintExceptionHandler.class)
public class ConstraintViolationExceptionHandler
        implements ExceptionHandler<ConstraintViolationException, HttpResponse<GenericError>> {

    @Override
    public HttpResponse<GenericError> handle(HttpRequest request, ConstraintViolationException exception) {
        var pairs = exception.getConstraintViolations().stream()
                // presence failures first, so a blank field reports "can't be blank" ahead of
                // a format error such as "is invalid"
                .sorted(Comparator.comparingInt(ConstraintViolationExceptionHandler::rank))
                .map(violation -> Map.entry(fieldOf(violation), messageOf(violation)))
                .toList();
        return HttpResponse.<GenericError>status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(GenericError.ofPairs(pairs));
    }

    /**
     * Resolves the response key from the property path, such as {@code email} for
     * {@code update.command.user.email} or {@code tagList} for a violation on one of its
     * elements.
     */
    static String fieldOf(ConstraintViolation<?> violation) {
        return ErrorFields.leaf(violation.getPropertyPath().toString());
    }

    /**
     * Ranks by the message template rather than the constraint annotation, which Micronaut
     * would synthesize as a proxy that native images cannot create.
     */
    private static int rank(ConstraintViolation<?> violation) {
        var template = requireNonNullElse(violation.getMessageTemplate(), "");
        return template.contains(NotNull.class.getName()) || template.contains(NotBlank.class.getName()) ? 0 : 1;
    }

    private static String messageOf(ConstraintViolation<?> violation) {
        return requireNonNullElse(violation.getMessage(), "is invalid");
    }

}
