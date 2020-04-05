package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetComment;
import com.github.al.realworld.api.query.GetCommentResult;
import com.github.al.realworld.application.CommentAssembler;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Comment;
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
public class GetCommentHandler implements QueryHandler<GetCommentResult, GetComment> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetCommentResult handle(GetComment query) {
        Article article = articleRepository.findBySlug(query.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exists", query.getSlug()));

        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElse(null);

        Comment comment = article.getComments().stream()
                .filter(c -> c.getId().equals(query.getId()))
                .findFirst()
                .orElseThrow(() -> notFound("comment [id=%s] does not exists", query.getId()));

        return new GetCommentResult(CommentAssembler.assemble(comment, currentProfile));
    }
}
