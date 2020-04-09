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

import com.github.al.bus.CommandHandler;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Objects;

import static com.github.al.realworld.application.exception.Exceptions.badRequest;
import static com.github.al.realworld.application.exception.Exceptions.forbidden;
import static com.github.al.realworld.application.exception.Exceptions.notFound;

@RequiredArgsConstructor
@Singleton
public class UpdateArticleHandler implements CommandHandler<UpdateArticleResult, UpdateArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public UpdateArticleResult handle(UpdateArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        if (!Objects.equals(article.getAuthor().getUsername(), command.getCurrentUsername())) {
            throw forbidden("article [slug=%s] is not owned by %s", command.getSlug(), command.getCurrentUsername());
        }

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> badRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        Article alteredArticle = article.toBuilder()
                .slug(command.getTitle() != null ? slugService.makeSlug(command.getTitle()) : article.getSlug())
                .title(command.getTitle() != null ? command.getTitle() : article.getTitle())
                .description(command.getDescription() != null ? command.getDescription() : article.getDescription())
                .body(command.getBody() != null ? command.getBody() : article.getBody())
                .updatedAt(ZonedDateTime.now())
                .build();

        articleRepository.save(alteredArticle);

        return new UpdateArticleResult(ArticleAssembler.assemble(alteredArticle, currentProfile));
    }
}
