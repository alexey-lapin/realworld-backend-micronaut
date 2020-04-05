package com.github.al.realworld.api.query;

import com.github.al.realworld.api.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetArticlesResult {

    private List<ArticleDto> articles;
    private Integer articlesCount;

}
