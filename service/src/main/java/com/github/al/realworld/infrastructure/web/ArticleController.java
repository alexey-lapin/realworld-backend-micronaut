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
package com.github.al.realworld.infrastructure.web;

import com.github.al.bus.Bus;
import com.github.al.realworld.api.command.AddComment;
import com.github.al.realworld.api.command.AddCommentResult;
import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.command.CreateArticleResult;
import com.github.al.realworld.api.command.DeleteArticle;
import com.github.al.realworld.api.command.DeleteComment;
import com.github.al.realworld.api.command.FavoriteArticle;
import com.github.al.realworld.api.command.FavoriteArticleResult;
import com.github.al.realworld.api.command.UnfavoriteArticle;
import com.github.al.realworld.api.command.UnfavoriteArticleResult;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateArticleResult;
import com.github.al.realworld.api.operation.ArticleOperations;
import com.github.al.realworld.api.query.GetArticle;
import com.github.al.realworld.api.query.GetArticleResult;
import com.github.al.realworld.api.query.GetArticles;
import com.github.al.realworld.api.query.GetArticlesResult;
import com.github.al.realworld.api.query.GetComments;
import com.github.al.realworld.api.query.GetCommentsResult;
import com.github.al.realworld.api.query.GetFeed;
import com.github.al.realworld.api.query.GetFeedResult;
import com.github.al.realworld.application.service.AuthenticationService;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.Valid;

@RequiredArgsConstructor
@Controller("${api.version}")
public class ArticleController implements ArticleOperations {

    private final Bus bus;
    private final AuthenticationService auth;

    @Override
    public GetArticlesResult findByFilters(@Nullable String tag,
                                           @Nullable String author,
                                           @Nullable String favorited,
                                           Integer limit,
                                           Integer offset) {
        return bus.executeQuery(new GetArticles(auth.currentUsername(), tag, author, favorited, limit, offset));
    }

    @Override
    public CreateArticleResult create(@Valid CreateArticle command) {
        return bus.executeCommand(command.withCurrentUsername(auth.currentUsername()));
    }

    @Override
    public GetFeedResult feed(Integer limit, Integer offset) {
        return bus.executeQuery(new GetFeed(auth.currentUsername(), limit, offset));
    }

    @Override
    public GetArticleResult findBySlug(String slug) {
        return bus.executeQuery(new GetArticle(auth.currentUsername(), slug));
    }

    @Override
    public UpdateArticleResult updateBySlug(String slug, @Valid UpdateArticle command) {
        return bus.executeCommand(command.withCurrentUsername(auth.currentUsername()).withSlug(slug));
    }

    @Override
    public void deleteBySlug(String slug) {
        bus.executeCommand(new DeleteArticle(auth.currentUsername(), slug));
    }

    @Override
    public FavoriteArticleResult favorite(String slug) {
        return bus.executeCommand(new FavoriteArticle(auth.currentUsername(), slug));
    }

    @Override
    public UnfavoriteArticleResult unfavorite(String slug) {
        return bus.executeCommand(new UnfavoriteArticle(auth.currentUsername(), slug));
    }

    @Override
    public GetCommentsResult findAllComments(String slug) {
        return bus.executeQuery(new GetComments(auth.currentUsername(), slug));
    }

    @Override
    public AddCommentResult addComment(String slug, @Valid AddComment command) {
        return bus.executeCommand(command.withCurrentUsername(auth.currentUsername()).withSlug(slug));
    }

    @Override
    public void deleteComment(String slug, Long id) {
        bus.executeCommand(new DeleteComment(auth.currentUsername(), slug, id));
    }

}
