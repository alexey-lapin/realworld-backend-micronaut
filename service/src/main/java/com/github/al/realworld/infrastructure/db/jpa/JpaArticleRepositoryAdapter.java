package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.repository.ArticleRepository;
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
    public List<Article> findByFilters(String tag, String author, String favorited) {
        return repository.findByFilters(tag, author, favorited);
    }

    @Override
    public List<Article> findByFollowees(List<String> followees) {
        return repository.findByFollowees(followees);
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
}
