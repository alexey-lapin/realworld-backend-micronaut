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
package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.UnfollowProfile;
import com.github.al.realworld.api.command.UnfollowProfileResult;
import com.github.al.realworld.application.ProfileAssembler;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.Exceptions.badRequest;
import static com.github.al.realworld.application.exception.Exceptions.notFound;

/**
 * follower - one who follows someone (current user)
 * followee - one who is followed
 */
@RequiredArgsConstructor
@Singleton
public class UnfollowProfileHandler implements CommandHandler<UnfollowProfileResult, UnfollowProfile> {

    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public UnfollowProfileResult handle(UnfollowProfile command) {
        Profile currentProfile = profileRepository.findByUsername(command.getFollower())
                .orElseThrow(() -> badRequest("user [name=%s] does not exist", command.getFollower()));

        Profile followee = profileRepository.findByUsername(command.getFollowee())
                .orElseThrow(() -> notFound("user [name=%s] does not exist", command.getFollowee()));

        Set<Profile> alteredFollowers = followee.getFollowers().stream()
                .filter(profile -> !Objects.equals(profile, currentProfile))
                .collect(Collectors.toSet());

        Profile alteredFollowee = followee.toBuilder()
                .clearFollowers()
                .followers(alteredFollowers)
                .build();

        Set<Profile> alteredFollowees = currentProfile.getFollowees().stream()
                .filter(profile -> !Objects.equals(profile, followee))
                .collect(Collectors.toSet());

        Profile alteredCurrentProfile = currentProfile.toBuilder()
                .clearFollowees()
                .followees(alteredFollowees)
                .build();
        profileRepository.save(alteredCurrentProfile);

        return new UnfollowProfileResult(ProfileAssembler.assemble(alteredFollowee, alteredCurrentProfile));
    }
}
