package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.UnfavoriteArticle;
import com.github.al.realworld.api.command.UnfavoriteArticleResult;
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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class UnfavoriteArticleHandler implements CommandHandler<UnfavoriteArticleResult, UnfavoriteArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UnfavoriteArticleResult handle(UnfavoriteArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));


        Set<Profile> alteredFavoritedProfiles = article.getFavoredProfiles().stream()
                .filter(favoritedProfile -> !Objects.equals(favoritedProfile, currentProfile))
                .collect(Collectors.toSet());

        Article alteredArticle = article.toBuilder()
                .clearFavoredProfiles()
                .favoredProfiles(alteredFavoritedProfiles)
                .build();

        Article savedArticle = articleRepository.save(alteredArticle);

        return new UnfavoriteArticleResult(ArticleAssembler.assemble(savedArticle, currentProfile));
    }
}
