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

import com.github.al.realworld.api.query.GetTags;
import com.github.al.realworld.api.query.GetTagsResult;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Tag;
import com.github.al.realworld.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Singleton
public class GetTagsHandler implements QueryHandler<GetTagsResult, GetTags> {

    private final TagRepository tagRepository;

    @Transactional
    @Override
    public GetTagsResult handle(GetTags query) {
        GetTagsResult result = new GetTagsResult(new ArrayList<>());

        StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .map(Tag::getName)
                .forEach(t -> result.getTags().add(t));

        return result;
    }
}
