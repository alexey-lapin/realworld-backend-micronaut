package com.github.al.realworld.infrastructure.web;

import com.github.al.bus.Bus;
import com.github.al.realworld.api.command.FollowProfile;
import com.github.al.realworld.api.command.FollowProfileResult;
import com.github.al.realworld.api.command.UnfollowProfile;
import com.github.al.realworld.api.command.UnfollowProfileResult;
import com.github.al.realworld.api.operation.ProfileOperations;
import com.github.al.realworld.api.query.GetProfile;
import com.github.al.realworld.api.query.GetProfileResult;
import com.github.al.realworld.application.service.AuthenticationService;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller("${api.version}")
public class ProfileController implements ProfileOperations {

    private final Bus bus;
    private final AuthenticationService auth;

    @Override
    public GetProfileResult findByUsername(String username) {
        return bus.executeQuery(new GetProfile(auth.currentUsername(), username));
    }

    @Override
    public FollowProfileResult follow(String username) {
        return bus.executeCommand(new FollowProfile(auth.currentUsername(), username));
    }

    @Override
    public UnfollowProfileResult unfollow(String username) {
        return bus.executeCommand(new UnfollowProfile(auth.currentUsername(), username));
    }

}
