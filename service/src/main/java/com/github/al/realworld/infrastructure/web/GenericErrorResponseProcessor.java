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
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.Error;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * Renders errors raised by the framework rather than the application, such as security
 * rejections and unreadable request bodies, in the {@link GenericError} envelope.
 *
 * <p>A 401 reports {@code token} as {@code is missing} when no {@code Authorization} header was
 * sent and {@code is invalid} otherwise. A 400, such as an unreadable body, becomes 422, as the
 * spec reports malformed requests that way; other statuses are kept. HEAD responses carry no
 * body.
 */
@Singleton
@Replaces(ErrorResponseProcessor.class)
public class GenericErrorResponseProcessor implements ErrorResponseProcessor<GenericError> {

    @Override
    @SuppressWarnings("unchecked")
    public MutableHttpResponse<GenericError> processResponse(ErrorContext errorContext,
                                                             MutableHttpResponse<?> response) {
        if (errorContext.getRequest().getMethod() == HttpMethod.HEAD) {
            return (MutableHttpResponse<GenericError>) response;
        }
        var status = response.status();
        GenericError body;
        if (status == HttpStatus.UNAUTHORIZED) {
            var authorization = errorContext.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            body = GenericError.of("token", authorization == null ? "is missing" : "is invalid");
        } else if (status == HttpStatus.FORBIDDEN) {
            body = GenericError.of(ErrorFields.FALLBACK, "forbidden");
        } else {
            if (status == HttpStatus.BAD_REQUEST) {
                response.status(HttpStatus.UNPROCESSABLE_ENTITY);
            }
            body = errorsOf(errorContext, status);
        }
        return response.contentType(MediaType.APPLICATION_JSON_TYPE).body(body);
    }

    private static GenericError errorsOf(ErrorContext errorContext, HttpStatus status) {
        if (!errorContext.hasErrors()) {
            // never echo a raw exception message for a server error
            var message = status.getCode() >= 500
                    ? status.getReason()
                    : errorContext.getExceptionMessage().orElse(status.getReason());
            return GenericError.of(ErrorFields.FALLBACK, message);
        }
        var pairs = errorContext.getErrors().stream()
                .map(error -> Map.entry(fieldOf(error), error.getMessage()))
                .toList();
        return GenericError.ofPairs(pairs);
    }

    private static String fieldOf(Error error) {
        return error.getPath().map(ErrorFields::leaf).orElse(ErrorFields.FALLBACK);
    }

}
