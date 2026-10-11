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
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.bus.CommandHandler;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.RequiredArgsConstructor;

import static com.github.al.realworld.application.exception.ForbiddenException.forbidden;
import static com.github.al.realworld.application.exception.NotFoundException.notFound;
import static com.github.al.realworld.api.dto.JsonNullable.unwrap;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
@Singleton
public class UpdateArticleHandler implements CommandHandler<UpdateArticleResult, UpdateArticle> {

    private final AuthenticationService authenticationService;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public UpdateArticleResult handle(UpdateArticle command) {
        var currentUserId = authenticationService.getRequiredCurrentUserId();
        var articleData = command.article();

        var article = articleRepository.findBySlug(command.slug())
                .orElseThrow(() -> notFound("article", "article [slug=%s] does not exist", command.slug()));

        if (article.authorId() != currentUserId) {
            throw forbidden("article", "article [slug=%s] is not owned by %s",
                    command.slug(), authenticationService.getCurrentUserName());
        }

        var newTitle = unwrap(articleData.title());
        var newDescription = unwrap(articleData.description());
        var newBody = unwrap(articleData.body());
        var newTagNames = unwrap(articleData.tagList());

        var builder = article.toBuilder()
                .slug(newTitle == null ? article.slug() : slugService.makeSlug(newTitle))
                .title(requireNonNullElse(newTitle, article.title()))
                .description(requireNonNullElse(newDescription, article.description()))
                .body(requireNonNullElse(newBody, article.body()));

        // a present tagList replaces the tags; an empty one removes them all
        if (newTagNames != null) {
            builder.clearTags().tags(resolveTags(newTagNames));
        }

        articleRepository.save(builder.build());

        var articleAssembly = articleRepository.findAssemblyById(currentUserId, article.id()).orElseThrow();
        return new UpdateArticleResult(ArticleAssembler.assemble(articleAssembly));
    }

    private List<Tag> resolveTags(List<String> names) {
        return names.stream()
                .distinct()
                .map(Tag::new)
                .map(tagRepository::saveOrGet)
                .sorted()
                .toList();
    }

}
