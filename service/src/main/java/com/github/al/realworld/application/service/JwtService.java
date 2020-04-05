package com.github.al.realworld.application.service;

import com.github.al.realworld.domain.model.User;

public interface JwtService {

    String getSubject(String token);

    String getToken(User user);

}
