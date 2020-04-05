package com.github.al.realworld.api.query;

import com.github.al.realworld.api.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetArticleResult {

    private ArticleDto article;

}
