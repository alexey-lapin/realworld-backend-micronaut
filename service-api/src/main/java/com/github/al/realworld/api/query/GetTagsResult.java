package com.github.al.realworld.api.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetTagsResult {

    private List<String> tags;

}
