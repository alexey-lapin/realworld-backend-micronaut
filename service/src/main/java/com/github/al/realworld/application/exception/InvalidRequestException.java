package com.github.al.realworld.application.exception;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public static InvalidRequestException invalidRequest(String message, Object... args) {
        return new InvalidRequestException(String.format(message, args));
    }
}
