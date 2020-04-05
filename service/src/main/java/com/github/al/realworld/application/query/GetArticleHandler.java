package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetArticle;
import com.github.al.realworld.api.query.GetArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class GetArticleHandler implements QueryHandler<GetArticleResult, GetArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetArticleResult handle(GetArticle query) {
        Article article = articleRepository.findBySlug(query.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exists", query.getSlug()));

        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElse(null);

        return new GetArticleResult(ArticleAssembler.assemble(article, currentProfile));
    }
}
