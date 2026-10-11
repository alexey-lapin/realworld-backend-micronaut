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

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.GenericRepository;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Reads articles from the {@code v_articles_read} view, computing {@code favorited} and
 * {@code author_following} for the current user, who may be anonymous.
 */
@JdbcRepository(dialect = Dialect.H2)
public interface ArticleAssemblyJdbcRepository extends GenericRepository<ArticleAssemblyJdbcEntity, Long> {

    // language=SQL
    String USER_COLUMNS = """
            case \
                when :currentUserId is null then false \
                else exists (select 1 \
                             from t_favorites fv \
                             where fv.article_id = ar.id \
                               and fv.user_id = :currentUserId) \
                end as favorited, \
            case \
                when :currentUserId is null then false \
                else exists (select 1 \
                             from t_follows fl \
                             where fl.followee_id = ar.author_id \
                               and fl.follower_id = :currentUserId) \
                end as author_following \
            """;

    // language=SQL
    String BASE_QUERY = """
            select ar.*, \
            """ + USER_COLUMNS + """
            from v_articles_read ar \
            """;

    // language=SQL
    String LIST_BASE_QUERY = """
            select ar.id, \
                   ar.created_at, \
                   ar.updated_at, \
                   ar.slug, \
                   ar.title, \
                   ar.description, \
                   null as body, \
                   ar.tag_list, \
                   ar.author_id, \
                   ar.favorites_count, \
                   ar.author_username, \
                   ar.author_bio, \
                   ar.author_image, \
            """ + USER_COLUMNS + """
            from v_articles_read ar \
            """;

    // language=SQL
    String FILTERS = """
            where (:authorId is null or ar.author_id = :authorId) \
              and (:tagId is null or \
                   exists (select 1 \
                           from t_article_tags at \
                           where at.article_id = ar.id \
                             and at.tag_id = :tagId)) \
              and (:favoritedById is null or \
                   exists (select 1 \
                           from t_favorites fv \
                           where fv.article_id = ar.id \
                             and fv.user_id = :favoritedById)) \
            """;

    @Query(BASE_QUERY + """
            where ar.id = :id""")
    Optional<ArticleAssemblyJdbcEntity> findById(long id, @Nullable Long currentUserId);

    @Query(BASE_QUERY + """
            where ar.slug = :slug""")
    Optional<ArticleAssemblyJdbcEntity> findBySlug(String slug, @Nullable Long currentUserId);

    // id breaks created_at ties, so pages stay stable
    @Query(LIST_BASE_QUERY + FILTERS + """
            order by ar.created_at desc, ar.id desc \
            limit :limit offset :offset""")
    List<ArticleAssemblyJdbcEntity> findFiltered(@Nullable Long tagId,
                                                 @Nullable Long authorId,
                                                 @Nullable Long favoritedById,
                                                 @Nullable Long currentUserId,
                                                 int limit,
                                                 long offset);

    @Query(LIST_BASE_QUERY + """
                join t_follows f on f.followee_id = ar.author_id \
            where f.follower_id = :currentUserId \
            order by ar.created_at desc, ar.id desc \
            limit :limit offset :offset""")
    List<ArticleAssemblyJdbcEntity> findFeed(long currentUserId, int limit, long offset);

    @Query("""
            select count(*) \
            from v_articles_read ar \
            """ + FILTERS)
    long countFiltered(@Nullable Long tagId, @Nullable Long authorId, @Nullable Long favoritedById);

    @Query("""
            select count(*) \
            from v_articles_read ar \
                     join t_follows f on f.followee_id = ar.author_id \
            where f.follower_id = :currentUserId""")
    long countFeed(long currentUserId);

}
