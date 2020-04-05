package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.DeleteComment;
import com.github.al.realworld.api.command.DeleteCommentResult;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.NoAuthorizationException.forbidden;
import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class DeleteCommentHandler implements CommandHandler<DeleteCommentResult, DeleteComment> {

    private final ArticleRepository articleRepository;

    @Transactional
    @Override
    public DeleteCommentResult handle(DeleteComment command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        Comment comment = article.getComments().stream()
                .filter(c -> c.getId().equals(command.getId()))
                .findFirst()
                .orElseThrow(() -> notFound("comment [id=%s] does not exist", command.getId()));

        if (!comment.getAuthor().getUsername().equals(command.getCurrentUsername())) {
            throw forbidden();
        }

        Set<Comment> alteredComments = article.getComments().stream()
                .filter(comment1 -> Objects.equals(comment1, comment))
                .collect(Collectors.toSet());

        Article alteredArticle = article.toBuilder().comments(alteredComments).build();
        articleRepository.save(alteredArticle);

        return new DeleteCommentResult();
    }
}
