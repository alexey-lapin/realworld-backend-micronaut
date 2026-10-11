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
import com.github.al.realworld.domain.model.ArticleAssembly;
import com.github.al.realworld.domain.model.ArticleItem;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class ArticleAssembler {

    private ArticleAssembler() {
    }

    public static ArticleDto assemble(ArticleAssembly article) {
        return new ArticleDto(
                article.slug(),
                article.title(),
                article.description(),
                article.body(),
                article.tagList(),
                zoned(article.createdAt()),
                zoned(article.updatedAt()),
                article.favorited(),
                article.favoritesCount(),
                ProfileAssembler.assemble(article.author()));
    }

    public static ArticleItemDto assembleItem(ArticleItem article) {
        return new ArticleItemDto(
                article.slug(),
                article.title(),
                article.description(),
                article.tagList(),
                zoned(article.createdAt()),
                zoned(article.updatedAt()),
                article.favorited(),
                article.favoritesCount(),
                ProfileAssembler.assemble(article.author()));
    }

    static ZonedDateTime zoned(Instant instant) {
        return instant.atZone(ZoneId.systemDefault());
    }

}
