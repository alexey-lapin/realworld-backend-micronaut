package com.github.al.realworld.domain.repository;

import com.github.al.realworld.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User save(User user);
}
