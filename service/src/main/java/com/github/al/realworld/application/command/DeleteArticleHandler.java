package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.DeleteArticle;
import com.github.al.realworld.api.command.DeleteArticleResult;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.util.Objects;

import static com.github.al.realworld.application.exception.NoAuthorizationException.forbidden;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class DeleteArticleHandler implements CommandHandler<DeleteArticleResult, DeleteArticle> {

    private final ArticleRepository articleRepository;

    @Transactional
    @Override
    public DeleteArticleResult handle(DeleteArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        if (!Objects.equals(article.getAuthor().getUsername(), command.getCurrentUsername())) {
            throw forbidden();
        }

        articleRepository.delete(article);

        return new DeleteArticleResult();
    }
}
