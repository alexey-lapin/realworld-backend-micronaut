package com.github.al.realworld.infrastructure.web;

import com.github.al.bus.Bus;
import com.github.al.realworld.api.operation.TagsOperations;
import com.github.al.realworld.api.query.GetTags;
import com.github.al.realworld.api.query.GetTagsResult;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller("${api.version}")
public class TagsController implements TagsOperations {

    private final Bus bus;

    @Override
    public GetTagsResult findAll() {
        return bus.executeQuery(new GetTags());
    }

}
