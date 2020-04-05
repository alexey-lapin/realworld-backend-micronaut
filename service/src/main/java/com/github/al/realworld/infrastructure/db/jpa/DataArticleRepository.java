package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Article;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataArticleRepository extends CrudRepository<Article, UUID> {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findByTitle(String title);

    @Query("SELECT DISTINCT a FROM Article a LEFT JOIN a.tags t LEFT JOIN a.author p LEFT JOIN a.favoredProfiles f WHERE " +
            "(:tag IS NULL OR t.name = :tag) AND " +
            "(:author IS NULL OR p.username = :author) AND " +
            "(:favorited IS NULL OR f.username = :favorited)")
    List<Article> findByFilters(@Nullable String tag,
                                @Nullable String author,
                                @Nullable String favorited);

    @Query("SELECT a FROM Article a JOIN a.author au WHERE au.username IN :followees")
    List<Article> findByFollowees(List<String> followees);

}
