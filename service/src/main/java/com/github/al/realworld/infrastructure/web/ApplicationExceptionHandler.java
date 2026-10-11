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
import com.github.al.realworld.application.exception.ApplicationException;
import com.github.al.realworld.application.exception.BadRequestException;
import com.github.al.realworld.application.exception.ConflictException;
import com.github.al.realworld.application.exception.ForbiddenException;
import com.github.al.realworld.application.exception.NotFoundException;
import com.github.al.realworld.application.exception.UnauthorizedException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Renders an {@link ApplicationException} as a {@link GenericError}, keyed by the field at fault.
 */
@Slf4j
@Produces
@Singleton
public class ApplicationExceptionHandler
        implements ExceptionHandler<ApplicationException, HttpResponse<GenericError>> {

    @Override
    public HttpResponse<GenericError> handle(HttpRequest request, ApplicationException exception) {
        log.debug("{} {} rejected: {}", request.getMethod(), request.getPath(), exception.getMessage());
        var status = switch (exception) {
            case BadRequestException _ -> HttpStatus.UNPROCESSABLE_ENTITY;
            case ConflictException _ -> HttpStatus.CONFLICT;
            case ForbiddenException _ -> HttpStatus.FORBIDDEN;
            case NotFoundException _ -> HttpStatus.NOT_FOUND;
            case UnauthorizedException _ -> HttpStatus.UNAUTHORIZED;
        };
        return HttpResponse.<GenericError>status(status)
                .body(GenericError.of(exception.field(), exception.error()));
    }

}
