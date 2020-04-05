package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JpaTagRepositoryAdapter implements TagRepository {

    private final DataTagRepository repository;

    @Override
    public Optional<Tag> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Iterable<Tag> findAll() {
        return repository.findAll();
    }
}
