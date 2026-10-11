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

import com.github.al.realworld.application.exception.NotFoundException;
import com.github.al.realworld.application.exception.ForbiddenException;
import com.github.al.realworld.application.exception.UnauthorizedException;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Tag;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateArticleHandlerTest {

    private static final String TEST_SLUG = "test-slug";
    private static final String TEST_USERNAME = "test-username";
    private static final String TEST_USERNAME_2 = "test-username-2";
    private static final String TEST_BODY = "test-body";
    private static final String ALTERED_TITLE = "altered-title";
    private static final String ALTERED_DESCRIPTION = "altered-description";
    private static final String ALTERED_BODY = "altered-body";

    private AuthenticationService authenticationService;
    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    private UpdateArticleHandler handler;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        articleRepository = mock(ArticleRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new UpdateArticleHandler(authenticationService,
                articleRepository,
                mock(TagRepository.class),
                userRepository,
                new SlugService());
        when(authenticationService.getRequiredCurrentUserName()).thenReturn(TEST_USERNAME);
    }

    @Test
    void should_throw404_when_articleDoesNotExist() {
        assertThatThrownBy(() -> handler.handle(command(emptyData())))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw403_when_articleIsNotOwnedByCurrentUser() {
        givenArticle();
        when(authenticationService.getRequiredCurrentUserName()).thenReturn(TEST_USERNAME_2);

        assertThatThrownBy(() -> handler.handle(command(emptyData())))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void should_throw401_when_currentUserDoesNotExist() {
        givenArticle();

        assertThatThrownBy(() -> handler.handle(command(emptyData())))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void should_returnCorrectArticleData_when_commandHasAllProperties() {
        givenArticle();
        givenCurrentUser();

        var data = new UpdateArticle.Data(
                new JsonNullable<>(ALTERED_TITLE),
                new JsonNullable<>(ALTERED_DESCRIPTION),
                new JsonNullable<>(ALTERED_BODY),
                new JsonNullable<>(List.of("tag2")));
        ArticleDto result = handler.handle(command(data)).article();

        assertThat(result.body()).isEqualTo(ALTERED_BODY);
        assertThat(result.title()).isEqualTo(ALTERED_TITLE);
        assertThat(result.description()).isEqualTo(ALTERED_DESCRIPTION);
        assertThat(result.slug()).isEqualTo(ALTERED_TITLE);
        assertThat(result.tagList()).containsExactly("tag2");
    }

    @Test
    void should_keepCurrentValues_when_commandHasNoProperties() {
        givenArticle();
        givenCurrentUser();

        ArticleDto result = handler.handle(command(emptyData())).article();

        assertThat(result.slug()).isEqualTo(TEST_SLUG);
        assertThat(result.body()).isEqualTo(TEST_BODY);
        assertThat(result.tagList()).containsExactly("tag1");
    }

    @Test
    void should_removeAllTags_when_tagListIsEmpty() {
        givenArticle();
        givenCurrentUser();

        var data = new UpdateArticle.Data(null, null, null, new JsonNullable<>(List.of()));
        ArticleDto result = handler.handle(command(data)).article();

        assertThat(result.tagList()).isEmpty();
    }

    private void givenArticle() {
        Article article = Article.builder()
                .slug(TEST_SLUG)
                .title("test-title")
                .description("test-description")
                .author(User.builder().username(TEST_USERNAME).build())
                .body(TEST_BODY)
                .tag(new Tag("tag1"))
                .build();
        when(articleRepository.findBySlug(TEST_SLUG)).thenReturn(Optional.of(article));
    }

    private void givenCurrentUser() {
        when(userRepository.findByUsername(TEST_USERNAME))
                .thenReturn(Optional.of(User.builder().username(TEST_USERNAME).build()));
    }

    private static UpdateArticle.Data emptyData() {
        return new UpdateArticle.Data(null, null, null, null);
    }

    private static UpdateArticle command(UpdateArticle.Data data) {
        return new UpdateArticle(TEST_SLUG, data);
    }

}
