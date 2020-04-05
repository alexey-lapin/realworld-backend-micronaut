package com.github.al.realworld.application.service;

import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@RequiredArgsConstructor
@Singleton
public class DefaultAuthenticationService implements AuthenticationService {

    private final SecurityService securityService;

    @SuppressWarnings("unchecked")
    @Override
    public String currentUsername() {
//        Optional<User> principal = (Optional<User>) SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal();
//        return principal.map(User::getUsername).orElse(null);
        return securityService.username().orElse(null);
    }

}
