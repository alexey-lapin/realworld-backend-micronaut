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
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.application.exception.ForbiddenException;
import com.github.al.realworld.application.exception.NotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateArticleHandlerTest {

    private static final String SLUG = "test-slug";
    private static final long AUTHOR_ID = 7;

    private AuthenticationService authenticationService;
    private ArticleRepository articleRepository;
    private TagRepository tagRepository;
    private UpdateArticleHandler handler;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        articleRepository = mock(ArticleRepository.class);
        tagRepository = mock(TagRepository.class);
        handler = new UpdateArticleHandler(authenticationService, articleRepository, tagRepository, new SlugService());
        when(authenticationService.getRequiredCurrentUserId()).thenReturn(AUTHOR_ID);
        when(tagRepository.saveOrGet(any())).thenAnswer(inv -> inv.getArgument(0));
        when(articleRepository.findAssemblyById(eq(AUTHOR_ID), anyLong())).thenReturn(Optional.of(assembly()));
    }

    @Test
    void should_throw404_when_articleDoesNotExist() {
        assertThatThrownBy(() -> handler.handle(command(data(null, null, null))))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void should_throw403_when_articleIsNotOwnedByCurrentUser() {
        givenArticle();
        when(authenticationService.getRequiredCurrentUserId()).thenReturn(AUTHOR_ID + 1);

        assertThatThrownBy(() -> handler.handle(command(data(null, null, null))))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void should_keepCurrentValues_when_fieldsAreAbsent() {
        givenArticle();

        var saved = handleAndCaptureSaved(data(null, null, null));

        assertThat(saved.slug()).isEqualTo(SLUG);
        assertThat(saved.title()).isEqualTo("title");
        assertThat(saved.tags()).extracting(Tag::name).containsExactly("old");
    }

    @Test
    void should_changeSlugAndReplaceTags_when_titleAndTagsArePresent() {
        givenArticle();

        var saved = handleAndCaptureSaved(data(new JsonNullable<>("New Title"), "body2", List.of("z", "a")));

        assertThat(saved.title()).isEqualTo("New Title");
        assertThat(saved.slug()).startsWith("new-title-");
        assertThat(saved.body()).isEqualTo("body2");
        assertThat(saved.tags()).extracting(Tag::name).containsExactly("a", "z");
    }

    @Test
    void should_removeAllTags_when_tagListIsEmpty() {
        givenArticle();

        var saved = handleAndCaptureSaved(data(null, null, List.of()));

        assertThat(saved.tags()).isEmpty();
    }

    private Article handleAndCaptureSaved(UpdateArticle.Data data) {
        handler.handle(command(data));
        var captor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepository).save(captor.capture());
        return captor.getValue();
    }

    private void givenArticle() {
        var article = Article.builder()
                .id(10)
                .authorId(AUTHOR_ID)
                .slug(SLUG)
                .title("title")
                .description("description")
                .body("body")
                .tag(new Tag(1, "old"))
                .build();
        when(articleRepository.findBySlug(SLUG)).thenReturn(Optional.of(article));
    }

    private static UpdateArticle.Data data(JsonNullable<String> title, String body, List<String> tags) {
        return new UpdateArticle.Data(title,
                null,
                body == null ? null : new JsonNullable<>(body),
                tags == null ? null : new JsonNullable<>(tags));
    }

    private static UpdateArticle command(UpdateArticle.Data data) {
        return new UpdateArticle(SLUG, data);
    }

    private static ArticleAssembly assembly() {
        return new ArticleAssembly(10, Instant.now(), Instant.now(), AUTHOR_ID, "slug", "title", "description",
                "body", List.of(), 0, false, new Profile("user", null, null, false));
    }

}
