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

import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.TagRepository;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.transaction.TransactionDefinition;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class TagJdbcRepositoryAdapter implements TagRepository {

    private final TagJdbcRepository repository;

    @Override
    public Optional<Tag> findByName(String name) {
        return repository.findByName(name).map(TagJdbcRepositoryAdapter::toDomain);
    }

    @Override
    public List<Tag> findAllByNameIn(Collection<String> names) {
        return repository.findByNameIn(names).stream().map(TagJdbcRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAll().stream().map(TagJdbcRepositoryAdapter::toDomain).toList();
    }

    /**
     * Inserts the tag in a transaction of its own, so that losing a race against a concurrent
     * insert of the same name fails only that transaction; the existing tag is then read back.
     */
    @Override
    @Transactional(propagation = TransactionDefinition.Propagation.REQUIRES_NEW)
    public Tag saveOrGet(Tag tag) {
        try {
            return toDomain(repository.save(new TagJdbcEntity(null, tag.name())));
        } catch (DataAccessException e) {
            if (JdbcErrors.isDuplicateKey(e)) {
                return findByName(tag.name()).orElseThrow(() -> e);
            }
            throw e;
        }
    }

    private static Tag toDomain(TagJdbcEntity entity) {
        return new Tag(JdbcIds.toDomainId(entity.id()), entity.name());
    }

}
