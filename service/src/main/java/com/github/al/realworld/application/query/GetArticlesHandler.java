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

import com.github.al.realworld.api.query.GetArticles;
import com.github.al.realworld.api.query.GetArticlesResult;
import com.github.al.realworld.application.ArticleAssembler;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.bus.QueryHandler;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.ArticleRepository;
import com.github.al.realworld.domain.repository.TagRepository;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Singleton
public class GetArticlesHandler implements QueryHandler<GetArticlesResult, GetArticles> {

    private final AuthenticationService authenticationService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    @Override
    public GetArticlesResult handle(GetArticles query) {
        var currentUserId = authenticationService.getCurrentUserId();

        // a filter naming an unknown tag or user matches nothing
        var tagId = resolve(query.tag(), name -> tagRepository.findByName(name).map(Tag::id));
        var authorId = resolve(query.author(), name -> userRepository.findByUsername(name).map(User::id));
        var favoritedById = resolve(query.favorited(), name -> userRepository.findByUsername(name).map(User::id));
        if (tagId.isEmpty() || authorId.isEmpty() || favoritedById.isEmpty()) {
            return new GetArticlesResult(List.of(), 0L);
        }

        var results = articleRepository.findAllItemsByFilters(currentUserId,
                        tagId.get().orElse(null),
                        authorId.get().orElse(null),
                        favoritedById.get().orElse(null),
                        query.limit(),
                        query.offset())
                .stream()
                .map(ArticleAssembler::assembleItem)
                .toList();

        var count = articleRepository.countByFilters(tagId.get().orElse(null),
                authorId.get().orElse(null),
                favoritedById.get().orElse(null));

        return new GetArticlesResult(results, count);
    }

    /**
     * Resolves an optional filter: empty when the filter names something unknown, otherwise the
     * id to filter by, or no id when the filter is absent.
     */
    private static Optional<Optional<Long>> resolve(@Nullable String filter,
                                                    Function<String, Optional<Long>> lookup) {
        if (filter == null) {
            return Optional.of(Optional.empty());
        }
        return lookup.apply(filter).map(Optional::of);
    }

}
