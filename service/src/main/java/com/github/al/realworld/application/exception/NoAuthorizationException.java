package com.github.al.realworld.application.exception;

//@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAuthorizationException extends RuntimeException {

    public NoAuthorizationException() {
    }

    public NoAuthorizationException(String message) {
        super(message);
    }

    public static NoAuthorizationException forbidden() {
        return new NoAuthorizationException();
    }

}
