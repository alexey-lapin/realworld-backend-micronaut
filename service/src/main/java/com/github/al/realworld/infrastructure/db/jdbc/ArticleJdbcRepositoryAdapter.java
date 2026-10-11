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

import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.ArticleAssembly;
import com.github.al.realworld.domain.model.ArticleItem;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.ArticleRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stores articles with their ordered tags: the tag names joined in {@code tag_list} for the read
 * view, and one {@code t_article_tags} row per tag, rewritten on every save.
 */
@RequiredArgsConstructor
@Singleton
public class ArticleJdbcRepositoryAdapter implements ArticleRepository {

    private final ArticleJdbcRepository repository;
    private final ArticleTagJdbcRepository articleTagRepository;
    private final ArticleAssemblyJdbcRepository assemblyRepository;

    @Override
    public Optional<Long> findIdBySlug(String slug) {
        return repository.findIdBySlug(slug);
    }

    @Override
    public Optional<Article> findBySlug(String slug) {
        return repository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public Article save(Article article) {
        var entity = new ArticleJdbcEntity(JdbcIds.toEntityId(article.id()),
                article.createdAt(),
                article.updatedAt(),
                article.authorId(),
                article.slug(),
                article.title(),
                article.description(),
                article.body(),
                tagList(article.tags()),
                article.favoritesCount());
        var saved = entity.id() == null ? repository.save(entity) : repository.update(entity);
        long articleId = saved.id();

        articleTagRepository.deleteByArticleId(articleId);
        var rows = new ArrayList<ArticleTagJdbcEntity>();
        for (var i = 0; i < article.tags().size(); i++) {
            rows.add(new ArticleTagJdbcEntity(null, articleId, article.tags().get(i).id(), i));
        }
        articleTagRepository.saveAll(rows);

        return article.toBuilder()
                .id(articleId)
                .createdAt(saved.createdAt())
                .updatedAt(saved.updatedAt())
                .build();
    }

    @Override
    public void incrementFavoriteCount(long id) {
        repository.incrementFavoriteCount(id);
    }

    @Override
    public void decrementFavoriteCount(long id) {
        repository.decrementFavoriteCount(id);
    }

    @Override
    public void deleteById(long id) {
        articleTagRepository.deleteByArticleId(id);
        repository.deleteById(id);
    }

    @Override
    public Optional<ArticleAssembly> findAssemblyById(@Nullable Long userId, long id) {
        return assemblyRepository.findById(id, userId).map(ArticleJdbcRepositoryAdapter::toAssembly);
    }

    @Override
    public Optional<ArticleAssembly> findAssemblyBySlug(@Nullable Long userId, String slug) {
        return assemblyRepository.findBySlug(slug, userId).map(ArticleJdbcRepositoryAdapter::toAssembly);
    }

    @Override
    public List<ArticleItem> findAllItemsByFilters(@Nullable Long userId,
                                                   @Nullable Long tagId,
                                                   @Nullable Long authorId,
                                                   @Nullable Long favoritedById,
                                                   int limit,
                                                   long offset) {
        return assemblyRepository.findFiltered(tagId, authorId, favoritedById, userId, limit, offset).stream()
                .map(ArticleJdbcRepositoryAdapter::toItem)
                .toList();
    }

    @Override
    public List<ArticleItem> findAllItemsByFollowerId(long userId, int limit, long offset) {
        return assemblyRepository.findFeed(userId, limit, offset).stream()
                .map(ArticleJdbcRepositoryAdapter::toItem)
                .toList();
    }

    @Override
    public long countByFilters(@Nullable Long tagId, @Nullable Long authorId, @Nullable Long favoritedById) {
        return assemblyRepository.countFiltered(tagId, authorId, favoritedById);
    }

    @Override
    public long countFeed(long userId) {
        return assemblyRepository.countFeed(userId);
    }

    private Article toDomain(ArticleJdbcEntity entity) {
        long articleId = entity.id();
        var names = tagNames(entity.tagList());
        var rows = articleTagRepository.findByArticleIdOrderByPosition(articleId);
        var tags = new ArrayList<Tag>();
        if (names.size() == rows.size()) {
            for (var i = 0; i < rows.size(); i++) {
                tags.add(new Tag(rows.get(i).tagId(), names.get(i)));
            }
        }
        return new Article(articleId,
                entity.createdAt(),
                entity.updatedAt(),
                entity.authorId(),
                entity.slug(),
                entity.title(),
                entity.description(),
                entity.body(),
                entity.favoritesCount(),
                tags);
    }

    private static ArticleAssembly toAssembly(ArticleAssemblyJdbcEntity entity) {
        return new ArticleAssembly(entity.id(),
                entity.createdAt(),
                entity.updatedAt(),
                entity.authorId(),
                entity.slug(),
                entity.title(),
                entity.description(),
                entity.body(),
                tagNames(entity.tagList()),
                entity.favoritesCount(),
                entity.favorited(),
                author(entity));
    }

    private static ArticleItem toItem(ArticleAssemblyJdbcEntity entity) {
        return new ArticleItem(entity.id(),
                entity.createdAt(),
                entity.updatedAt(),
                entity.authorId(),
                entity.slug(),
                entity.title(),
                entity.description(),
                tagNames(entity.tagList()),
                entity.favoritesCount(),
                entity.favorited(),
                author(entity));
    }

    private static Profile author(ArticleAssemblyJdbcEntity entity) {
        return new Profile(entity.authorUsername(), entity.authorBio(), entity.authorImage(), entity.authorFollowing());
    }

    private static @Nullable String tagList(List<Tag> tags) {
        return tags.isEmpty() ? null : tags.stream().map(Tag::name).collect(Collectors.joining(","));
    }

    private static List<String> tagNames(@Nullable String tagList) {
        return tagList == null ? List.of() : Arrays.asList(tagList.split(","));
    }

}
