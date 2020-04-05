package com.github.al.realworld.infrastructure.config.security;

import io.micronaut.security.token.reader.HttpHeaderTokenReader;

import javax.inject.Singleton;

@Singleton
public class JwtReader extends HttpHeaderTokenReader {

    @Override
    protected String getPrefix() {
        return "Token";
    }

    @Override
    protected String getHeaderName() {
        return "Authorization";
    }
}
