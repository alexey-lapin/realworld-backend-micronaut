package com.github.al.realworld.infrastructure.config.security;

import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;

@RequiredArgsConstructor
@Singleton
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    @Override
    public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
//        authenticationRequest.getIdentity()
        return null;
    }
}
