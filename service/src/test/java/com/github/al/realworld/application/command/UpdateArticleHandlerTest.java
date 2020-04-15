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

import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateArticleResult;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateArticleHandlerTest {

    public static final String TEST_SLUG = "test-slug";
    public static final String TEST_USERNAME = "test-username";
    public static final String TEST_USERNAME_2 = "test-username-2";
    public static final String TEST_BODY = "test-body";
    public static final String ALTERED_TITLE = "altered-title";
    public static final String ALTERED_DESCRIPTION = "altered-description";
    public static final String ALTERED_BODY = "altered-body";

    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    private UpdateArticleHandler handler;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new UpdateArticleHandler(articleRepository, userRepository, new SlugService());
    }

    @Test
    void should_throw404_when_articleDoesNotExist() {
        UpdateArticle command = UpdateArticle.builder().slug(TEST_SLUG).build();

        HttpStatusException throwable = catchThrowableOfType(
                () -> handler.handle(command),
                HttpStatusException.class
        );

        assertThat(throwable.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void should_throw400_when_articleDoesNotOwnedByCurrentUser() {
        Article article = Article.builder()
                .slug(TEST_SLUG)
                .author(Profile.builder().username(TEST_USERNAME).build())
                .body(TEST_BODY)
                .build();
        when(articleRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(article));

        UpdateArticle command = UpdateArticle.builder()
                .slug(TEST_SLUG)
                .currentUsername(TEST_USERNAME_2)
                .build();

        HttpStatusException throwable = catchThrowableOfType(
                () -> handler.handle(command),
                HttpStatusException.class
        );

        assertThat(throwable.getStatus().getCode()).isEqualTo(HttpStatus.FORBIDDEN.getCode());
    }

    @Test
    void should_throw400_when_currentUserDoesNotExist() {
        Article article = Article.builder()
                .slug(TEST_SLUG)
                .author(Profile.builder().username(TEST_USERNAME).build())
                .body(TEST_BODY)
                .build();
        when(articleRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(article));

        UpdateArticle command = UpdateArticle.builder()
                .slug(TEST_SLUG)
                .currentUsername(TEST_USERNAME)
                .build();

        HttpStatusException throwable = catchThrowableOfType(
                () -> handler.handle(command),
                HttpStatusException.class
        );

        assertThat(throwable.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    void should_returnCorrectArticleData_when_commandHasAllProperties() {
        Article article = Article.builder()
                .slug(TEST_SLUG)
                .author(Profile.builder().username(TEST_USERNAME).build())
                .body(TEST_BODY)
                .build();
        when(articleRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(article));

        User user = User.builder()
                .username(TEST_USERNAME)
                .profile(Profile.builder().username(TEST_USERNAME).build())
                .build();
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        UpdateArticle command = UpdateArticle.builder()
                .slug(TEST_SLUG)
                .title(ALTERED_TITLE)
                .description(ALTERED_DESCRIPTION)
                .body(ALTERED_BODY)
                .currentUsername(TEST_USERNAME)
                .build();

        ArticleDto result = handler.handle(command).getArticle();

        assertThat(result.getBody()).isEqualTo(ALTERED_BODY);
        assertThat(result.getTitle()).isEqualTo(ALTERED_TITLE);
        assertThat(result.getDescription()).isEqualTo(ALTERED_DESCRIPTION);
        assertThat(result.getSlug()).isEqualTo(ALTERED_TITLE);
    }

    @Test
    void should_returnCorrectArticleData_when_commandDoesNotHaveAllProperties() {
        Article article = Article.builder()
                .slug(TEST_SLUG)
                .author(Profile.builder().username(TEST_USERNAME).build())
                .body(TEST_BODY)
                .build();
        when(articleRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(article));

        User user = User.builder()
                .username(TEST_USERNAME)
                .profile(Profile.builder().username(TEST_USERNAME).build())
                .build();
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        UpdateArticleResult result = handler.handle(UpdateArticle.builder()
                .slug(TEST_SLUG)
                .currentUsername(TEST_USERNAME)
                .build());

        assertThat(result.getArticle().getBody()).isEqualTo(TEST_BODY);
    }
}
