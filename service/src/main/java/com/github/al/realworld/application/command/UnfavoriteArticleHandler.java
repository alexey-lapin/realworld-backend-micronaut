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

import com.github.al.realworld.api.command.UnfavoriteArticle;
import com.github.al.realworld.api.command.UnfavoriteArticleResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.bus.CommandHandler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.Exceptions.badRequest;
import static com.github.al.realworld.application.exception.Exceptions.notFound;

@RequiredArgsConstructor
@Singleton
public class UnfavoriteArticleHandler implements CommandHandler<UnfavoriteArticleResult, UnfavoriteArticle> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UnfavoriteArticleResult handle(UnfavoriteArticle command) {
        Article article = articleRepository.findBySlug(command.getSlug())
                .orElseThrow(() -> notFound("article [slug=%s] does not exist", command.getSlug()));

        User currentUser = userRepository.findByUsername(command.getCurrentUsername())
                .orElseThrow(() -> badRequest("user [name=%s] does not exist", command.getCurrentUsername()));

        Set<User> alteredFavoritedProfiles = article.getFavoredUsers().stream()
                .filter(favoritedUser -> !Objects.equals(favoritedUser, currentUser))
                .collect(Collectors.toSet());

        Article alteredArticle = article.toBuilder()
                .clearFavoredUsers()
                .favoredUsers(alteredFavoritedProfiles)
                .build();

        Article savedArticle = articleRepository.save(alteredArticle);

        return new UnfavoriteArticleResult(ArticleAssembler.assemble(savedArticle, currentUser));
    }

}
