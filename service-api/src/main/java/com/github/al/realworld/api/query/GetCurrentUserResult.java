package com.github.al.realworld.api.query;

import com.github.al.realworld.api.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCurrentUserResult {

    private UserDto user;

}
