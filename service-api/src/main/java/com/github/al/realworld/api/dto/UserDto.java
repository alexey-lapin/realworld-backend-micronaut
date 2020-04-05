package com.github.al.realworld.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private String email;
    private String token;
    private String username;
    private String bio;
    private String image;

}
