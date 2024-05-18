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

import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.dto.CreateArticleDto;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateArticleHandlerTest {

    private ArticleRepository articleRepository;
    private TagRepository tagRepository;
    private UserRepository userRepository;
    private CreateArticleHandler handler;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        tagRepository = mock(TagRepository.class);
        userRepository = mock(UserRepository.class);
        handler = new CreateArticleHandler(articleRepository, tagRepository, userRepository, new SlugService());
    }

    @Test
    void should_throw400_when_articleAlreadyExist() {
        when(articleRepository.findByTitle("test-title")).thenReturn(Optional.of(Article.builder().build()));

        CreateArticleDto data = CreateArticleDto.builder()
                .title("test-title")
                .build();
        HttpStatusException exception = catchThrowableOfType(
                () -> handler.handle(new CreateArticle(null, data)),
                HttpStatusException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    void should_throw400_when_currentUsernameDoesNotExist() {
        CreateArticleDto data = CreateArticleDto.builder()
                .title("test-title")
                .build();
        HttpStatusException exception = catchThrowableOfType(
                () -> handler.handle(new CreateArticle(null, data)),
                HttpStatusException.class
        );

        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
    }

    @Test
    void should_returnCorrectArticleData() {
        User user = User.builder()
                .username("test-username")
                .build();
        when(userRepository.findByUsername("test-username")).thenReturn(Optional.of(user));

        when(articleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateArticleDto data = CreateArticleDto.builder()
                .title("test-title")
                .description("test-description")
                .body("test-body")
                .tagList(Arrays.asList("tag1", "tag2"))
                .build();
        ArticleDto result = handler.handle(new CreateArticle("test-username", data)).getArticle();

        assertThat(result.getSlug()).isEqualTo("test-title");
        assertThat(result.getTitle()).isEqualTo("test-title");
        assertThat(result.getDescription()).isEqualTo("test-description");
        assertThat(result.getBody()).isEqualTo("test-body");
        assertThat(result.getAuthor().getUsername()).isEqualTo("test-username");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getTagList()).containsExactlyInAnyOrder("tag1", "tag2");
    }
}
