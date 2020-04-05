package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.FollowProfile;
import com.github.al.realworld.api.command.FollowProfileResult;
import com.github.al.realworld.application.ProfileAssembler;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

/**
 * follower - one who follows someone (current user)
 * followee - is one who is followed
 */
@RequiredArgsConstructor
@Singleton
public class FollowProfileHandler implements CommandHandler<FollowProfileResult, FollowProfile> {

    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public FollowProfileResult handle(FollowProfile command) {
        Profile currentProfile = profileRepository.findByUsername(command.getFollower())
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getFollower()));

        Profile followee = profileRepository.findByUsername(command.getFollowee())
                .orElseThrow(() -> notFound("user [name=%s] does not exist", command.getFollowee()));

        Profile alteredFollowee = followee.toBuilder().follower(currentProfile).build();

        Profile alteredCurrentProfile = currentProfile.toBuilder().followee(followee).build();
        profileRepository.save(alteredCurrentProfile);

        return new FollowProfileResult(ProfileAssembler.assemble(alteredFollowee, alteredCurrentProfile));
    }
}
