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

import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface ProfileJdbcRepository extends GenericRepository<UserJdbcEntity, Long> {

    @Query("""
            select u.username, u.bio, u.image, \
                   case \
                       when :currentUserId is null then false \
                       else exists (select 1 \
                                    from t_follows fl \
                                    where fl.followee_id = u.id \
                                      and fl.follower_id = :currentUserId) \
                       end as following \
            from t_users u \
            where u.username = :username""")
    Optional<ProfileJdbcView> findByUsername(String username, @Nullable Long currentUserId);

}
