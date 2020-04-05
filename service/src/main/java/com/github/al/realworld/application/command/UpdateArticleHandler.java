package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.time.ZonedDateTime;
import java.util.Objects;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.NoAuthorizationException.forbidden;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class UpdateArticleHandler implements CommandHandler<UpdateArticleResult, UpdateArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public UpdateArticleResult handle(UpdateArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        if (!Objects.equals(article.getAuthor().getUsername(), command.getCurrentUsername())) {
            throw forbidden();
        }

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        Article alteredArticle = article.toBuilder()
                .slug(command.getTitle() != null ? slugService.makeSlug(command.getTitle()) : article.getSlug())
                .title(command.getTitle() != null ? command.getTitle() : article.getTitle())
                .description(command.getDescription() != null ? command.getDescription() : article.getDescription())
                .body(command.getBody() != null ? command.getBody() : article.getBody())
                .updatedAt(ZonedDateTime.now())
                .build();

        articleRepository.save(alteredArticle);

        return new UpdateArticleResult(ArticleAssembler.assemble(alteredArticle, currentProfile));
    }
}
