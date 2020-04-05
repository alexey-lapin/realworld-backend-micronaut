package com.github.al.realworld.api.query;

import com.github.al.bus.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCurrentUser implements Query<GetCurrentUserResult> {

    private String username;

}
