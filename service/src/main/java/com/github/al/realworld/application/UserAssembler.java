package com.github.al.realworld.application;

import com.github.al.realworld.api.dto.UserDto;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.domain.model.User;

public class UserAssembler {

    public static UserDto assemble(User user, JwtService jwtService) {
        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .token(jwtService.getToken(user))
                .build();
    }

}
