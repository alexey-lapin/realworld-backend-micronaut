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

import com.github.al.bus.QueryHandler;
import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.api.query.GetFeed;
import com.github.al.realworld.api.query.GetFeedResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.FollowRelation;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.FollowRelationRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import jakarta.inject.Singleton;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.al.realworld.application.exception.Exceptions.badRequest;

@RequiredArgsConstructor
@Singleton
public class GetFeedHandler implements QueryHandler<GetFeedResult, GetFeed> {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final FollowRelationRepository followRelationRepository;

    @Transactional
    @Override
    public GetFeedResult handle(GetFeed query) {
        User currentUser = userRepository.findByUsername(query.getCurrentUsername())
                .orElseThrow(() -> badRequest("user [name=%s] does not exist", query.getCurrentUsername()));

        List<FollowRelation> relations = followRelationRepository.findByFollowerId(currentUser.getId());

        List<UUID> followeesUsernames = relations.stream()
                .map(followRelation -> followRelation.getFollowee().getId())
                .collect(Collectors.toList());

        List<Article> articles = articleRepository
                .findByFollowees(followeesUsernames, query.getLimit(), query.getOffset());

        ArrayList<ArticleDto> results = new ArrayList<>();

        articles.forEach(article -> results.add(ArticleAssembler.assemble(article, currentUser)));

        return new GetFeedResult(results, results.size());
    }

}
