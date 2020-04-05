package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.FavoriteArticle;
import com.github.al.realworld.api.command.FavoriteArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class FavoriteArticleHandler implements CommandHandler<FavoriteArticleResult, FavoriteArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public FavoriteArticleResult handle(FavoriteArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        Article alteredArticle = article.toBuilder()
                .favoredProfile(currentProfile)
                .build();

        Article savedArticle = articleRepository.save(alteredArticle);

        return new FavoriteArticleResult(ArticleAssembler.assemble(savedArticle, currentProfile));
    }
}
