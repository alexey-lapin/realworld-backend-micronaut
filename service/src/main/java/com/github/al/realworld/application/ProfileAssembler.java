package com.github.al.realworld.application;

import com.github.al.realworld.api.dto.ProfileDto;
import com.github.al.realworld.domain.model.Profile;

public class ProfileAssembler {

    public static ProfileDto assemble(Profile profile, Profile currentProfile) {
        boolean isFollow = currentProfile != null && profile.getFollowers().contains(currentProfile);
        return new ProfileDto(profile.getUsername(), profile.getBio(), profile.getImage(), isFollow);
    }

}
