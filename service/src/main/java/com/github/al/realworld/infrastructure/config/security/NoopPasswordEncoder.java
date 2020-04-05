package com.github.al.realworld.infrastructure.config.security;

import io.micronaut.security.authentication.providers.PasswordEncoder;

import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class NoopPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return Objects.equals(rawPassword, encodedPassword);
    }
}
