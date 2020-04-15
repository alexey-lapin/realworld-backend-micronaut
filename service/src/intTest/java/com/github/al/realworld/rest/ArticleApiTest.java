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
package com.github.al.realworld.rest;

import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.operation.ArticleClient;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static com.github.al.realworld.rest.auth.AuthSupport.login;
import static com.github.al.realworld.rest.auth.AuthSupport.register;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class ArticleApiTest {

    @Inject
    private ArticleClient articleClient;

    @Test
    void should_returnCorrectArticleData() {
        String user = register();
        login(user);

        CreateArticle command = CreateArticle.builder()
                .title("test-title")
                .description("test-description")
                .body("test-body")
                .build();

        ArticleDto article = articleClient.create(command).getArticle();

        assertThat(article.getSlug()).isEqualTo("test-title");
        assertThat(article.getTitle()).isEqualTo("test-title");
        assertThat(article.getDescription()).isEqualTo("test-description");
        assertThat(article.getBody()).isEqualTo("test-body");
        assertThat(article.getAuthor().getUsername()).isEqualTo(user);
    }

}
