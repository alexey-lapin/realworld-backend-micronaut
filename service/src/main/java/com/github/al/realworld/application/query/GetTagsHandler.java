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
