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
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;


import static com.github.al.realworld.application.exception.ForbiddenException.forbidden;
import static com.github.al.realworld.application.exception.UnauthorizedException.invalidToken;
import static com.github.al.realworld.application.exception.NotFoundException.notFound;
import static com.github.al.realworld.api.dto.JsonNullable.unwrap;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
@Singleton
public class UpdateArticleHandler implements CommandHandler<UpdateArticleResult, UpdateArticle> {

    private final AuthenticationService authenticationService;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public UpdateArticleResult handle(UpdateArticle command) {
        var currentUsername = authenticationService.getRequiredCurrentUserName();

        Article article = articleRepository.findBySlug(command.slug())
                .orElseThrow(() -> notFound("article", "article [slug=%s] does not exist", command.slug()));

        if (!Objects.equals(article.getAuthor().getUsername(), currentUsername)) {
            throw forbidden("article", "article [slug=%s] is not owned by %s", command.slug(), currentUsername);
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> invalidToken("user [name=%s] does not exist", currentUsername));

        var data = command.article();
        var newTitle = unwrap(data.title());
        var newDescription = unwrap(data.description());
        var newBody = unwrap(data.body());
        var newTagNames = unwrap(data.tagList());

        Article.ArticleBuilder builder = article.toBuilder()
                .slug(newTitle == null ? article.getSlug() : slugService.makeSlug(newTitle))
                .title(requireNonNullElse(newTitle, article.getTitle()))
                .description(requireNonNullElse(newDescription, article.getDescription()))
                .body(requireNonNullElse(newBody, article.getBody()))
                .updatedAt(ZonedDateTime.now());

        // a present tagList replaces the tags; an empty one removes them all
        if (newTagNames != null) {
            builder.clearTags().tags(resolveTags(newTagNames));
        }

        Article alteredArticle = builder.build();
        articleRepository.save(alteredArticle);

        return new UpdateArticleResult(ArticleAssembler.assemble(alteredArticle, currentUser));
    }

    private List<Tag> resolveTags(List<String> names) {
        return names.stream()
                .distinct()
                .map(name -> tagRepository.findByName(name).orElseGet(() -> new Tag(name)))
                .toList();
    }

}
