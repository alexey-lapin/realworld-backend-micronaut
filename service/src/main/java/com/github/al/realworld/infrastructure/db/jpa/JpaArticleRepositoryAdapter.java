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
package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.repository.ArticleRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JpaArticleRepositoryAdapter implements ArticleRepository {

    private final DataArticleRepository repository;

    @Override
    public Optional<Article> findBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    @Override
    public Optional<Article> findByTitle(String title) {
        return repository.findByTitle(title);
    }

    @Override
    public List<Article> findByFilters(String tag, String author, String favorited, Integer limit, Integer offset) {
        OffsetBasedPageable pageable = new OffsetBasedPageable(limit, offset, Sort.of(Sort.Order.desc("createdAt")));
        return repository.findByFilters(tag, author, favorited, pageable);
    }

    @Override
    public List<Article> findByFollowees(List<String> followees, Integer limit, Integer offset) {
        OffsetBasedPageable pageable = new OffsetBasedPageable(limit, offset, Sort.of(Sort.Order.desc("createdAt")));
        return repository.findByFollowees(followees, pageable);
    }

    @Override
    public void delete(Article article) {
        repository.delete(article);
    }

    @Override
    public Article save(Article article) {
        if (repository.existsById(article.getId())) {
            return repository.update(article);
        }
        return repository.save(article);
    }

    @RequiredArgsConstructor
    @Getter
    private static class OffsetBasedPageable implements Pageable {
        private final int size;
        private int number;
        private final long offset;
        private final Sort sort;
    }

}
