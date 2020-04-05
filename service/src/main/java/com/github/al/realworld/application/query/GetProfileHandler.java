package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetProfile;
import com.github.al.realworld.api.query.GetProfileResult;
import com.github.al.realworld.application.ProfileAssembler;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class GetProfileHandler implements QueryHandler<GetProfileResult, GetProfile> {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetProfileResult handle(GetProfile query) {
        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElse(null);

        Profile profile = userRepository.findByUsername(query.getUsername())
                .map(User::getProfile)
                .orElseThrow(() -> notFound("user [name=%s] does not exist", query.getUsername()));

        return new GetProfileResult(ProfileAssembler.assemble(profile, currentProfile));
    }
}
