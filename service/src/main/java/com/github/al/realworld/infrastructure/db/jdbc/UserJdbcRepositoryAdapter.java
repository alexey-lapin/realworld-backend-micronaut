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
package com.github.al.realworld.infrastructure.db.jdbc;

import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class UserJdbcRepositoryAdapter implements UserRepository {

    private final UserJdbcRepository repository;

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public Optional<User> findById(long id) {
        return repository.findById(id).map(UserJdbcRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserJdbcRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserJdbcRepositoryAdapter::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = new UserJdbcEntity(JdbcIds.toEntityId(user.id()),
                user.createdAt(),
                user.updatedAt(),
                user.username(),
                user.email(),
                user.password(),
                user.bio(),
                user.image());
        var saved = entity.id() == null ? repository.save(entity) : repository.update(entity);
        return toDomain(saved);
    }

    private static User toDomain(UserJdbcEntity entity) {
        return new User(JdbcIds.toDomainId(entity.id()),
                entity.createdAt(),
                entity.updatedAt(),
                entity.username(),
                entity.email(),
                entity.password(),
                entity.bio(),
                entity.image());
    }

}
