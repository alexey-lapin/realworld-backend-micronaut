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

import com.github.al.realworld.application.exception.ConflictException;
import com.github.al.realworld.application.exception.UnauthorizedException;
import com.github.al.realworld.application.exception.UnauthorizedException;
import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateArticleHandlerTest {

    private static final String TEST_USERNAME = "test-username";

    private AuthenticationService authenticationService;
    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    private CreateArticleHandler handler;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        articleRepository = mock(ArticleRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new CreateArticleHandler(authenticationService,
                articleRepository,
                mock(TagRepository.class),
                userRepository,
                new SlugService());
    }

    @Test
    void should_throw409_when_articleAlreadyExist() {
        when(articleRepository.findByTitle("test-title")).thenReturn(Optional.of(Article.builder().build()));

        assertThatThrownBy(() -> handler.handle(command("test-title", null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void should_throw401_when_notAuthenticated() {
        when(authenticationService.getRequiredCurrentUserName()).thenCallRealMethod();

        assertThatThrownBy(() -> handler.handle(command("test-title", null)))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_throw401_when_currentUserDoesNotExist() {
        when(authenticationService.getRequiredCurrentUserName()).thenReturn(TEST_USERNAME);

        assertThatThrownBy(() -> handler.handle(command("test-title", null)))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_returnCorrectArticleData() {
        when(authenticationService.getRequiredCurrentUserName()).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME))
                .thenReturn(Optional.of(User.builder().username(TEST_USERNAME).build()));
        when(articleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArticleDto result = handler.handle(command("test-title", List.of("tag1", "tag2"))).article();

        assertThat(result.slug()).isEqualTo("test-title");
        assertThat(result.title()).isEqualTo("test-title");
        assertThat(result.description()).isEqualTo("test-description");
        assertThat(result.body()).isEqualTo("test-body");
        assertThat(result.author().username()).isEqualTo(TEST_USERNAME);
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.updatedAt()).isNotNull();
        assertThat(result.tagList()).containsExactly("tag1", "tag2");
    }

    private static CreateArticle command(String title, List<String> tagList) {
        return new CreateArticle(new CreateArticle.Data(title, "test-description", "test-body", tagList));
    }

}
