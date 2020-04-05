package com.github.al.realworld.application.query;

import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.query.GetArticles;
import com.github.al.realworld.api.query.GetArticlesResult;
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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Singleton
public class GetArticlesHandler implements QueryHandler<GetArticlesResult, GetArticles> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetArticlesResult handle(GetArticles query) {
        List<Article> articles = articleRepository
                .findByFilters(query.getTag(), query.getAuthor(), query.getFavorited());

        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElse(null);

        List<ArticleDto> results = new ArrayList<>();

        articles.forEach(article -> results.add(ArticleAssembler.assemble(article, currentProfile)));

        return new GetArticlesResult(results, results.size());
    }
}
