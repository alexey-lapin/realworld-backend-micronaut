package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.util.Optional;

@Repository
public interface DataUserRepository extends CrudRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(@Nullable String username);

}
