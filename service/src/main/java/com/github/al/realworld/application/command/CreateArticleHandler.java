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
import com.github.al.realworld.api.command.CreateArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.SlugService;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;

import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class CreateArticleHandler implements CommandHandler<CreateArticleResult, CreateArticle> {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SlugService slugService;

    @Transactional
    @Override
    public CreateArticleResult handle(CreateArticle command) {
        Optional<Article> articleByTitleOptional = articleRepository.findByTitle(command.getTitle());
        if (articleByTitleOptional.isPresent()) {
            throw invalidRequest("article [title=%s] already exists", command.getTitle());
        }

        Profile currentProfile = userRepository.findByUsername(command.getCurrentUsername())
                .map(User::getProfile)
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        ZonedDateTime now = ZonedDateTime.now();

        Article.ArticleBuilder articleBuilder = Article.builder()
                .id(UUID.randomUUID())
                .slug(slugService.makeSlug(command.getTitle()))
                .title(command.getTitle())
                .description(command.getDescription())
                .body(command.getBody())
                .createdAt(now)
                .updatedAt(now)
                .author(currentProfile);

        command.getTagList().stream()
                .map(t -> tagRepository.findByName(t).orElseGet(() -> new Tag(t)))
                .forEach(articleBuilder::tag);

        Article savedArticle = articleRepository.save(articleBuilder.build());

        return new CreateArticleResult(ArticleAssembler.assemble(savedArticle, currentProfile));
    }
}
