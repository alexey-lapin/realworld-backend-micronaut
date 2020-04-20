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
package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetComment;
import com.github.al.realworld.api.query.GetCommentResult;
import com.github.al.realworld.application.CommentAssembler;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.Exceptions.notFound;

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

        User currentUser = userRepository.findByUsername(query.getCurrentUsername())
                .orElse(null);

        Comment comment = article.getComments().stream()
                .filter(c -> c.getId().equals(query.getId()))
                .findFirst()
                .orElseThrow(() -> notFound("comment [id=%s] does not exists", query.getId()));

        return new GetCommentResult(CommentAssembler.assemble(comment, currentUser));
    }
}
