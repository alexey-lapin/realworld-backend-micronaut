package com.github.al.realworld.domain.repository;

import com.github.al.realworld.domain.model.Tag;

import java.util.Optional;

public interface TagRepository {

    Optional<Tag> findByName(String name);

    Iterable<Tag> findAll();
}
