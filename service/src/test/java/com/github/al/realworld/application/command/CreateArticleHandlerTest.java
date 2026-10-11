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
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.ArticleAssembly;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateArticleHandlerTest {

    private static final long USER_ID = 7;

    private AuthenticationService authenticationService;
    private ArticleRepository articleRepository;
    private TagRepository tagRepository;
    private CreateArticleHandler handler;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        articleRepository = mock(ArticleRepository.class);
        tagRepository = mock(TagRepository.class);
        handler = new CreateArticleHandler(authenticationService, articleRepository, tagRepository, new SlugService());
    }

    @Test
    void should_saveArticleWithSortedDistinctTags() {
        when(authenticationService.getRequiredCurrentUserId()).thenReturn(USER_ID);
        when(tagRepository.findAllByNameIn(anyCollection())).thenReturn(List.of(new Tag(1, "b")));
        when(tagRepository.saveOrGet(any())).thenAnswer(inv -> new Tag(2, inv.<Tag>getArgument(0).name()));
        when(articleRepository.save(any())).thenAnswer(inv -> inv.<Article>getArgument(0).toBuilder().id(10).build());
        when(articleRepository.findAssemblyById(eq(USER_ID), anyLong())).thenReturn(Optional.of(assembly()));

        handler.handle(command("Hello World", List.of("b", "a", "b")));

        var captor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.authorId()).isEqualTo(USER_ID);
        assertThat(saved.slug()).startsWith("hello-world-");
        assertThat(saved.tags()).extracting(Tag::name).containsExactly("a", "b");
    }

    private static CreateArticle command(String title, List<String> tagList) {
        return new CreateArticle(new CreateArticle.Data(title, "description", "body", tagList));
    }

    private static ArticleAssembly assembly() {
        return new ArticleAssembly(10, Instant.now(), Instant.now(), USER_ID, "slug", "title", "description", "body",
                List.of(), 0, false, new Profile("user", null, null, false));
    }

}
