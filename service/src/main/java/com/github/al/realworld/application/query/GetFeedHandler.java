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
package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetFeed;
import com.github.al.realworld.api.query.GetFeedResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.bus.QueryHandler;
import com.github.al.realworld.domain.repository.ArticleRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class GetFeedHandler implements QueryHandler<GetFeedResult, GetFeed> {

    private final AuthenticationService authenticationService;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    @Override
    public GetFeedResult handle(GetFeed query) {
        var currentUserId = authenticationService.getRequiredCurrentUserId();

        var results = articleRepository.findAllItemsByFollowerId(currentUserId, query.limit(), query.offset()).stream()
                .map(ArticleAssembler::assembleItem)
                .toList();

        return new GetFeedResult(results, articleRepository.countFeed(currentUserId));
    }

}
