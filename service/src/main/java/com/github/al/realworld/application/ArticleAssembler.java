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
package com.github.al.realworld.application;

import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.dto.ArticleItemDto;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.model.User;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ArticleAssembler {

    public static ArticleDto assemble(Article article, @Nullable User currentUser) {
        return new ArticleDto(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getBody(),
                tagList(article),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited(article, currentUser),
                article.getFavoredUsers().size(),
                ProfileAssembler.assemble(article.getAuthor(), currentUser));
    }

    public static ArticleItemDto assembleItem(Article article, @Nullable User currentUser) {
        return new ArticleItemDto(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                tagList(article),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited(article, currentUser),
                article.getFavoredUsers().size(),
                ProfileAssembler.assemble(article.getAuthor(), currentUser));
    }

    private static List<String> tagList(Article article) {
        return article.getTags().stream().map(Tag::getName).sorted().toList();
    }

    private static boolean isFavorited(Article article, @Nullable User currentUser) {
        return currentUser != null && article.getFavoredUsers().contains(currentUser);
    }

}
