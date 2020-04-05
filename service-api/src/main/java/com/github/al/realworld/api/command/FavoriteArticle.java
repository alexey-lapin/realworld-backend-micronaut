package com.github.al.realworld.api.command;

import com.github.al.bus.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FavoriteArticle implements Command<FavoriteArticleResult> {

    private String currentUsername;
    private String slug;

}
