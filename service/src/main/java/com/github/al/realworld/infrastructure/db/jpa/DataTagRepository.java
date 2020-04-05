package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Tag;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface DataTagRepository extends CrudRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

}
