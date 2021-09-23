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
package com.github.al.realworld.api.operation;

import com.github.al.realworld.api.command.AddComment;
import com.github.al.realworld.api.command.AddCommentResult;
import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.command.CreateArticleResult;
import com.github.al.realworld.api.command.FavoriteArticleResult;
import com.github.al.realworld.api.command.UnfavoriteArticleResult;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateArticleResult;
import com.github.al.realworld.api.query.GetArticleResult;
import com.github.al.realworld.api.query.GetArticlesResult;
import com.github.al.realworld.api.query.GetCommentResult;
import com.github.al.realworld.api.query.GetCommentsResult;
import com.github.al.realworld.api.query.GetFeedResult;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;

import javax.validation.Valid;

public interface ArticleOperations {

    @Get("/articles{?tag,author,favorited,limit,offset}")
    GetArticlesResult findByFilters(@QueryValue(value = "tag") @Nullable String tag,
                                    @QueryValue(value = "author") @Nullable String author,
                                    @QueryValue(value = "favourited") @Nullable String favorited,
                                    @QueryValue(value = "limit", defaultValue = "20") Integer limit,
                                    @QueryValue(value = "offset", defaultValue = "0") Integer offset);

    @Status(HttpStatus.CREATED)
    @Post("/articles")
    CreateArticleResult create(@Valid @Body CreateArticle command);

    @Get("/articles/feed")
    GetFeedResult feed(@QueryValue(value = "limit", defaultValue = "20") Integer limit,
                       @QueryValue(value = "offset", defaultValue = "0") Integer offset);

    @Get("/articles/{slug}")
    GetArticleResult findBySlug(@PathVariable("slug") String slug);

    @Put("/articles/{slug}")
    UpdateArticleResult updateBySlug(@PathVariable("slug") String slug, @Valid @Body UpdateArticle command);

    @Status(HttpStatus.NO_CONTENT)
    @Delete("/articles/{slug}")
    void deleteBySlug(@PathVariable("slug") String slug);

    @Post("/articles/{slug}/favorite")
    FavoriteArticleResult favorite(@PathVariable("slug") String slug);

    @Delete("/articles/{slug}/favorite")
    UnfavoriteArticleResult unfavorite(@PathVariable("slug") String slug);

    @Get("/articles/{slug}/comments")
    GetCommentsResult findAllComments(@PathVariable("slug") String slug);

    @Get("/articles/{slug}/comments/{id}")
    GetCommentResult findComment(@PathVariable("slug") String slug, @PathVariable("id") Long id);

    @Post("/articles/{slug}/comments")
    AddCommentResult addComment(@PathVariable("slug") String slug, @Valid @Body AddComment command);

    @Status(HttpStatus.NO_CONTENT)
    @Delete("/articles/{slug}/comments/{id}")
    void deleteComment(@PathVariable("slug") String slug, @PathVariable("id") Long id);

}
