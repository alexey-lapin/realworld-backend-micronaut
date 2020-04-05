package com.github.al.realworld.api.operation;

import com.github.al.realworld.api.command.FollowProfileResult;
import com.github.al.realworld.api.command.UnfollowProfileResult;
import com.github.al.realworld.api.query.GetProfileResult;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

public interface ProfileOperations {

    @Get("/profiles/{username}")
    GetProfileResult findByUsername(@PathVariable String username);

    @Post("/profiles/{username}/follow")
    FollowProfileResult follow(@PathVariable String username);

    @Delete("/profiles/{username}/follow")
    UnfollowProfileResult unfollow(@PathVariable String username);

}
