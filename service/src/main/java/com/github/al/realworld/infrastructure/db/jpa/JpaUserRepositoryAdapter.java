package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JpaUserRepositoryAdapter implements UserRepository {

    private final DataUserRepository repository;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        if (repository.existsById(user.getUsername())) {
            return repository.update(user);
        }
        return repository.save(user);
    }
}
