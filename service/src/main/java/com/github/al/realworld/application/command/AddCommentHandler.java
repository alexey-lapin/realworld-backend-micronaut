/*
 * MIT License
 *
 * Copyright (c) 2020 - present Alexey Lapin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.AddComment;
import com.github.al.realworld.api.command.AddCommentResult;
import com.github.al.realworld.application.CommentAssembler;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.bus.CommandHandler;
import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.CommentRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import static com.github.al.realworld.application.exception.NotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class AddCommentHandler implements CommandHandler<AddCommentResult, AddComment> {

    private final AuthenticationService authenticationService;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public AddCommentResult handle(AddComment command) {
        var currentUserId = authenticationService.getRequiredCurrentUserId();

        var articleId = articleRepository.findIdBySlug(command.slug())
                .orElseThrow(() -> notFound("article", "article [slug=%s] does not exist", command.slug()));

        var comment = Comment.builder()
                .articleId(articleId)
                .authorId(currentUserId)
                .body(command.comment().body())
                .build();
        var savedComment = commentRepository.save(comment);

        var commentAssembly = commentRepository.findAssemblyById(currentUserId, savedComment.id()).orElseThrow();
        return new AddCommentResult(CommentAssembler.assemble(commentAssembly));
    }

}
