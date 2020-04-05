package com.github.al.realworld.api.command;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.github.al.bus.Command;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Introspected
@JsonRootName("article")
public class CreateArticle implements Command<CreateArticleResult> {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String body;
    private List<String> tagList;
    @With
    private String currentUsername;

}
