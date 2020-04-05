package com.github.al.realworld.api.command;

import com.github.al.realworld.api.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateArticleResult {

    private ArticleDto article;

}
