package com.github.al.realworld.api.command;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.github.al.bus.Command;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Introspected
@JsonRootName("article")
public class UpdateArticle implements Command<UpdateArticleResult> {

    @With
    private String slug;
    private String title;
    private String description;
    private String body;
    @With
    private String currentUsername;

}
