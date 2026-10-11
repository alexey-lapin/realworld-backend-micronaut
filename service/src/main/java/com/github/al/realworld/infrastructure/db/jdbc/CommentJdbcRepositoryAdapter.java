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

import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.model.CommentAssembly;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.repository.CommentRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class CommentJdbcRepositoryAdapter implements CommentRepository {

    private final CommentJdbcRepository repository;
    private final CommentAssemblyJdbcRepository assemblyRepository;

    @Override
    public Optional<Comment> findById(long id) {
        return repository.findById(id).map(CommentJdbcRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<CommentAssembly> findAssemblyById(@Nullable Long userId, long id) {
        return assemblyRepository.findById(id, userId).map(CommentJdbcRepositoryAdapter::toAssembly);
    }

    @Override
    public List<CommentAssembly> findAllAssemblyByArticleId(@Nullable Long userId, long articleId) {
        return assemblyRepository.findAllByArticleId(articleId, userId).stream()
                .map(CommentJdbcRepositoryAdapter::toAssembly)
                .toList();
    }

    @Override
    public Comment save(Comment comment) {
        var entity = new CommentJdbcEntity(JdbcIds.toEntityId(comment.id()),
                comment.createdAt(),
                comment.updatedAt(),
                comment.articleId(),
                comment.authorId(),
                comment.body());
        var saved = entity.id() == null ? repository.save(entity) : repository.update(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAllByArticleId(long articleId) {
        repository.deleteByArticleId(articleId);
    }

    private static Comment toDomain(CommentJdbcEntity entity) {
        return new Comment(JdbcIds.toDomainId(entity.id()),
                entity.createdAt(),
                entity.updatedAt(),
                entity.articleId(),
                entity.authorId(),
                entity.body());
    }

    private static CommentAssembly toAssembly(CommentAssemblyJdbcEntity entity) {
        return new CommentAssembly(entity.id(),
                entity.createdAt(),
                entity.updatedAt(),
                entity.articleId(),
                entity.authorId(),
                entity.body(),
                new Profile(entity.authorUsername(), entity.authorBio(), entity.authorImage(), entity.authorFollowing()));
    }

}
