package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JpaProfileRepositoryAdapter implements ProfileRepository {

    private final DataProfileRepository repository;

    @Override
    public Optional<Profile> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Profile save(Profile profile) {
        if (repository.existsById(profile.getUsername())) {
            return repository.update(profile);
        }
        return repository.save(profile);
    }
}
