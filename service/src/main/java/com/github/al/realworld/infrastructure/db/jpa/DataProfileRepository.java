package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Profile;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface DataProfileRepository extends CrudRepository<Profile, String> {

    Optional<Profile> findByUsername(String username);

}
