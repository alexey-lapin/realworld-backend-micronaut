package com.github.al.realworld.api.operation;

import com.github.al.realworld.api.query.GetTagsResult;
import io.micronaut.http.annotation.Get;

public interface TagsOperations {

    @Get("/tags")
    GetTagsResult findAll();

}
