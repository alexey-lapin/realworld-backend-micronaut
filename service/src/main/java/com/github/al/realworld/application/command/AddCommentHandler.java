package com.github.al.realworld.application.command;

import com.github.al.bus.CommandHandler;
import com.github.al.realworld.api.command.AddComment;
import com.github.al.realworld.api.command.AddCommentResult;
import com.github.al.realworld.application.CommentAssembler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class AddCommentHandler implements CommandHandler<AddCommentResult, AddComment> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AddCommentResult handle(AddComment command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        Profile profile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        ZonedDateTime now = ZonedDateTime.now();

        Comment comment = Comment.builder()
                .body(command.getBody())
                .createdAt(now)
                .updatedAt(now)
                .author(profile)
                .build();

        Article alteredArticle = article.toBuilder().comment(comment).build();

        articleRepository.save(alteredArticle);

        return new AddCommentResult(CommentAssembler.assemble(comment, profile));
    }
}
