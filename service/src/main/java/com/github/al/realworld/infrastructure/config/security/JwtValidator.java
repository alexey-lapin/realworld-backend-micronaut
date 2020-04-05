package com.github.al.realworld.infrastructure.config.security;

import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.DefaultAuthentication;
import io.micronaut.security.token.validator.TokenValidator;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JwtValidator implements TokenValidator {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Publisher<Authentication> validateToken(String token) {
        String subject = jwtService.getSubject(token);
        Optional<User> userOptional = userRepository.findByUsername(subject);
        if (userOptional.isPresent()) {
            return Flowable.just(new DefaultAuthentication(subject, Collections.emptyMap()));
        }
        return Flowable.empty();
    }
}
