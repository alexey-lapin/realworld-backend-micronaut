package com.github.al.realworld.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProfileDto {

    private String username;
    private String bio;
    private String image;
    private Boolean following;

}
