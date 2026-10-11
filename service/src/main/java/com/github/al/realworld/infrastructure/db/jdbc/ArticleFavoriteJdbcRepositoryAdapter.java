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

import com.github.al.realworld.domain.model.ArticleFavorite;
import com.github.al.realworld.domain.repository.ArticleFavoriteRepository;
import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class ArticleFavoriteJdbcRepositoryAdapter implements ArticleFavoriteRepository {

    private final ArticleFavoriteJdbcRepository repository;

    /**
     * Inserts only when absent; a concurrent insert that wins the race surfaces as a duplicate key,
     * which also means the favorite exists.
     */
    @Override
    public boolean add(ArticleFavorite articleFavorite) {
        try {
            return repository.insertIfAbsent(articleFavorite.articleId(), articleFavorite.userId()) > 0;
        } catch (DataAccessException e) {
            if (JdbcErrors.isDuplicateKey(e)) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public boolean remove(ArticleFavorite articleFavorite) {
        return repository.deleteByArticleIdAndUserId(articleFavorite.articleId(), articleFavorite.userId()) > 0;
    }

    @Override
    public void deleteAllByArticleId(long articleId) {
        repository.deleteByArticleId(articleId);
    }

}
