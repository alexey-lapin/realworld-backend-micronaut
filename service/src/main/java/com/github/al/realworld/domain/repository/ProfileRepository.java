package com.github.al.realworld.domain.repository;

import com.github.al.realworld.domain.model.Profile;

import java.util.Optional;

public interface ProfileRepository {

    Optional<Profile> findByUsername(String username);

    Profile save(Profile profile);
}
