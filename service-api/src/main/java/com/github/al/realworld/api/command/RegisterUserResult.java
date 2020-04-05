package com.github.al.realworld.api.command;

import com.github.al.realworld.api.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterUserResult {

    private UserDto user;

}
