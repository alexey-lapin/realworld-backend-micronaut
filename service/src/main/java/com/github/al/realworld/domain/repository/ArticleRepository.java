package com.github.al.realworld.domain.repository;

import com.github.al.realworld.domain.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findByTitle(String title);

    List<Article> findByFilters(String tag, String author, String favorited);

    List<Article> findByFollowees(List<String> followees);

    void delete(Article article);

    Article save(Article article);
}
