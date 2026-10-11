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
package com.github.al.realworld.rest;

import com.github.al.realworld.api.command.AddComment;
import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.dto.CommentDto;
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.api.operation.ArticleClient;
import com.github.al.realworld.api.operation.ProfileClient;
import com.github.al.realworld.api.query.GetArticleResult;
import com.github.al.realworld.api.query.GetArticlesResult;
import com.github.al.realworld.api.query.GetFeedResult;
import com.github.al.realworld.infrastructure.db.jpa.DataArticleRepository;
import com.github.al.realworld.rest.auth.AuthSupport;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.github.al.realworld.rest.auth.AuthSupport.logout;
import static com.github.al.realworld.rest.auth.AuthSupport.register;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@MicronautTest
public class ArticleApiTest {

    public static final String TEST_TITLE = "test-title";
    public static final String TEST_DESCRIPTION = "test-description";
    public static final String TEST_BODY = "test-body";
    public static final String ALTERED_TITLE = "altered-title";
    public static final String ALTERED_BODY = "altered-body";
    public static final String ALTERED_DESCRIPTION = "altered-description";

    @Inject
    private ArticleClient articleClient;

    @Inject
    private DataArticleRepository articleRepository;

    @AfterEach
    void afterEach() {
        logout();
    }

    @Test
    void should_returnCorrectArticleData() {
        String user = register().login().username();

        CreateArticle command = createArticleCommand();
        ArticleDto article = articleClient.create(command).article();

        assertThat(article.slug()).isEqualTo(command.article().title());
        assertThat(article.title()).isEqualTo(command.article().title());
        assertThat(article.description()).isEqualTo(command.article().description());
        assertThat(article.body()).isEqualTo(command.article().body());
        assertThat(article.author().username()).isEqualTo(user);
    }

    @Test
    void should_returnCorrectArticleData_when_favoriteAndUnfavorite() {
        String user = register().login().username();

        ArticleDto created = articleClient.create(createArticleCommand()).article();

        ArticleDto favoritedArticle = articleClient.favorite(created.slug()).article();
        assertThat(favoritedArticle.favorited()).isTrue();
        assertThat(favoritedArticle.favoritesCount()).isEqualTo(1);

        ArticleDto unfavoritedArticle = articleClient.unfavorite(created.slug()).article();
        assertThat(unfavoritedArticle.favorited()).isFalse();
        assertThat(unfavoritedArticle.favoritesCount()).isEqualTo(0);
    }

    @Test
    void should_returnCorrectArticleData_when_delete() {
        String user = register().login().username();

        ArticleDto created = articleClient.create(createArticleCommand()).article();

        articleClient.deleteBySlug(created.slug());

        GetArticleResult found = articleClient.findBySlug(created.slug());
        assertThat(found).isNull();
    }

    @Test
    void should_throw403_when_deleteNotOwned() {
        register().login();

        ArticleDto created = articleClient.create(createArticleCommand()).article();

        register().login();

        HttpClientResponseException exception = catchThrowableOfType(
                () -> articleClient.deleteBySlug(created.slug()),
                HttpClientResponseException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.FORBIDDEN.getCode());
    }

    @Test
    void should_returnCorrectArticleData_when_deleteNotExisting() {
        register().login();

        articleClient.deleteBySlug("not-existing");
    }

    @Test
    void should_returnCorrectArticleData_when_update() {
        register().login();

        ArticleDto created = articleClient.create(createArticleCommand()).article();

        var data = new UpdateArticle.Data(
                new JsonNullable<>(ALTERED_TITLE),
                new JsonNullable<>(ALTERED_DESCRIPTION),
                new JsonNullable<>(ALTERED_BODY),
                null);
        UpdateArticle updateArticle = new UpdateArticle(null, data);
        ArticleDto updated = articleClient.updateBySlug(created.slug(), updateArticle).article();

        assertThat(updated.slug()).isEqualTo(ALTERED_TITLE);
        assertThat(updated.title()).isEqualTo(ALTERED_TITLE);
        assertThat(updated.description()).isEqualTo(ALTERED_DESCRIPTION);
        assertThat(updated.body()).isEqualTo(ALTERED_BODY);
    }

    @Test
    void should_returnCorrectArticleData_when_searchByFilters() {
        GetArticlesResult r1 = articleClient.findByFilters(null, null, null, 20, 0);
        System.out.println(r1.articlesCount());

        articleRepository.deleteAll();

        GetArticlesResult r2 = articleClient.findByFilters(null, null, null, 20, 0);
        System.out.println(r2.articlesCount());

        String user = register().login().username();

        articleClient.create(createArticleCommand(List.of("tag1")));

        GetArticlesResult result2 = articleClient.findByFilters(null, user, null, 20, 0);
        assertThat(result2.articlesCount()).isEqualTo(1);

        GetArticlesResult result3 = articleClient.findByFilters(null, UUID.randomUUID().toString(), null, 20, 0);
        assertThat(result3.articlesCount()).isEqualTo(0);

        GetArticlesResult result4 = articleClient.findByFilters("tag1", null, null, 20, 0);
        assertThat(result4.articlesCount()).isEqualTo(1);

        GetArticlesResult result5 = articleClient.findByFilters("tag2", null, null, 20, 0);
        assertThat(result5.articlesCount()).isEqualTo(0);

        GetArticlesResult result6 = articleClient.findByFilters("tag1", user, null, 20, 0);
        assertThat(result6.articlesCount()).isEqualTo(1);
    }

    @Nested
    class CommentTest {

        @Test
        void should_returnCorrectCommentData_whenCreateDeleteComment() {
            String user = register().login().username();

            ArticleDto created = articleClient.create(createArticleCommand()).article();

            AddComment addComment = new AddComment(null, new AddComment.Data(TEST_BODY));

            CommentDto comment = articleClient.addComment(created.slug(), addComment).comment();

            assertThat(comment.id()).isPositive();
            assertThat(comment.author().username()).isEqualTo(user);
            assertThat(comment.body()).isEqualTo(TEST_BODY);
            assertThat(comment.createdAt()).isNotNull();
            assertThat(comment.updatedAt()).isNotNull();

            List<CommentDto> comments = articleClient.findAllComments(created.slug()).comments();

            assertThat(comments).hasSize(1);

            articleClient.deleteComment(created.slug(), comment.id());

            comments = articleClient.findAllComments(created.slug()).comments();

            assertThat(comments).isEmpty();
        }

        @Test
        void should_throw403_when_commentIsNotOwned() {
            register().login();

            ArticleDto article = articleClient.create(createArticleCommand()).article();

            AddComment addComment = new AddComment(null, new AddComment.Data(TEST_BODY));
            CommentDto comment = articleClient.addComment(article.slug(), addComment).comment();

            register().login();

            HttpClientResponseException exception = catchThrowableOfType(
                    () -> articleClient.deleteComment(article.slug(), comment.id()),
                    HttpClientResponseException.class
            );

            assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.FORBIDDEN.getCode());
        }

        @Test
        void should_throw404_when_addCommentArticleDoesNotExist() {
            register().login();

            AddComment addComment = new AddComment(null, new AddComment.Data(TEST_BODY));

            articleClient.addComment(UUID.randomUUID().toString(), addComment);
        }

        @Test
        void should_throw404_when_deleteCommentArticleDoesNotExist() {
            register().login();

            articleClient.deleteComment(UUID.randomUUID().toString(), 9999999L);
        }

        @Test
        void should_throw404_when_deleteCommentDoesNotExist() {
            register().login();

            ArticleDto article = articleClient.create(createArticleCommand()).article();

            articleClient.deleteComment(article.slug(), 9999999L);
        }

        @Test
        void should_returnCorrectCommentData() {
            register().login();
            ArticleDto article = articleClient.create(createArticleCommand()).article();
            AddComment addComment = new AddComment(null, new AddComment.Data(TEST_BODY));
            CommentDto createdComment = articleClient.addComment(article.slug(), addComment).comment();

            CommentDto comment = articleClient.findComment(article.slug(), createdComment.id()).comment();

            assertThat(comment.id()).isPositive();
        }
    }

    @Nested
    class FeetTest {

        @Inject
        private ProfileClient profileClient;

        @Test
        void should_returnEmptyFeed_when_userDoesNotFollowAnyone() {
            register().login();

            GetFeedResult feed = articleClient.feed(10, 0);

            assertThat(feed.articlesCount()).isEqualTo(0);
        }

        @Test
        void should_returnEmptyFeed_when_userHasOwnArticleAndDoesNotFollowAnyone() {
            register().login();

            articleClient.create(createArticleCommand());

            GetFeedResult feed = articleClient.feed(10, 0);

            assertThat(feed.articlesCount()).isEqualTo(0);
        }

        @Test
        void should_returnCorrectFeed_when_userHasOwnArticleAndFollowSomeone() {
            AuthSupport.RegisteredUser user1 = register().login();
            articleClient.create(createArticleCommand());
            GetFeedResult feed1 = articleClient.feed(10, 0);
            assertThat(feed1.articlesCount()).isEqualTo(0);
            logout();

            AuthSupport.RegisteredUser user2 = register().login();
            articleClient.create(createArticleCommand());
            articleClient.create(createArticleCommand());
            logout();

            user1.login();
            profileClient.follow(user2.username());
            GetFeedResult feed2 = articleClient.feed(10, 0);
            assertThat(feed2.articlesCount()).isEqualTo(2);
        }

        @Test
        void should_returnCorrectFeed_when_userHasOwnArticleAndFollowSomeOtherUsers() {
            AuthSupport.RegisteredUser user1 = register().login();
            articleClient.create(createArticleCommand());
            GetFeedResult feed1 = articleClient.feed(10, 0);
            assertThat(feed1.articlesCount()).isEqualTo(0);
            logout();

            AuthSupport.RegisteredUser user2 = register().login();
            articleClient.create(createArticleCommand());
            articleClient.create(createArticleCommand());
            logout();

            AuthSupport.RegisteredUser user3 = register().login();
            articleClient.create(createArticleCommand());
            articleClient.create(createArticleCommand());
            articleClient.create(createArticleCommand());
            logout();

            user1.login();
            profileClient.follow(user2.username());
            profileClient.follow(user3.username());
            GetFeedResult feed2 = articleClient.feed(4, 0);
            assertThat(feed2.articlesCount()).isEqualTo(4);
        }
    }

    private static CreateArticle createArticleCommand() {
        return createArticleCommand(null);
    }

    private static CreateArticle createArticleCommand(List<String> tagList) {
        var data = new CreateArticle.Data(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                tagList);
        return new CreateArticle(data);
    }

}
