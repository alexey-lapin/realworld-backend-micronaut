/*
 * MIT License
 *
 * Copyright (c) 2020 - present Alexey Lapin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Tag(name = "Profiles")
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
