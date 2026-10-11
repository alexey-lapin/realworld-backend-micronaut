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

import com.github.al.realworld.domain.model.FollowRelation;
import com.github.al.realworld.domain.repository.FollowRelationRepository;
import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Singleton
public class FollowRelationJdbcRepositoryAdapter implements FollowRelationRepository {

    private final FollowRelationJdbcRepository repository;

    @Override
    public List<FollowRelation> findByFollowerId(long followerId) {
        return repository.findByFollowerId(followerId).stream().map(FollowRelationJdbcRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<FollowRelation> findByFolloweeId(long followeeId) {
        return repository.findByFolloweeId(followeeId).stream().map(FollowRelationJdbcRepositoryAdapter::toDomain).toList();
    }

    @Override
    public boolean add(FollowRelation followRelation) {
        try {
            return repository.insertIfAbsent(followRelation.followerId(), followRelation.followeeId()) > 0;
        } catch (DataAccessException e) {
            if (JdbcErrors.isDuplicateKey(e)) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public boolean remove(FollowRelation followRelation) {
        return repository.deleteByFollowerIdAndFolloweeId(followRelation.followerId(), followRelation.followeeId()) > 0;
    }

    private static FollowRelation toDomain(FollowRelationJdbcEntity entity) {
        return new FollowRelation(entity.followerId(), entity.followeeId());
    }

}
