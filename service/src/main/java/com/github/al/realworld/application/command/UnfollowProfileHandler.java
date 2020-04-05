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

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

/**
 * follower - one who follows someone (current user)
 * followee - is one who is followed
 */
@RequiredArgsConstructor
@Singleton
public class UnfollowProfileHandler implements CommandHandler<UnfollowProfileResult, UnfollowProfile> {

    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public UnfollowProfileResult handle(UnfollowProfile command) {
        Profile currentProfile = profileRepository.findByUsername(command.getFollower())
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getFollower()));

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
