package com.github.al.realworld.application.query;

import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.query.GetFeed;
import com.github.al.realworld.api.query.GetFeedResult;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class GetFeedHandler implements QueryHandler<GetFeedResult, GetFeed> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetFeedResult handle(GetFeed query) {
        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", query.getCurrentUsername()));

        Set<Profile> followees = currentProfile.getFollowees();

        List<Article> articles = articleRepository
                .findByFollowees(followees.stream().map(Profile::getUsername).collect(Collectors.toList()));

        ArrayList<ArticleDto> results = new ArrayList<>();

        articles.forEach(article -> results.add(ArticleAssembler.assemble(article, currentProfile)));

        return new GetFeedResult(results, results.size());
    }
}
