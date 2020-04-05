package com.github.al.realworld.application;

import com.github.al.realworld.api.dto.ArticleDto;
import com.github.al.realworld.domain.model.Article;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.Tag;

import java.util.stream.Collectors;

public class ArticleAssembler {

    public static ArticleDto assemble(Article article, Profile currentProfile) {
        return ArticleDto.builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .tagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .favorited(currentProfile != null && article.getFavoredProfiles().contains(currentProfile))
                .favoritesCount(article.getFavoredProfiles().size())
                .author(ProfileAssembler.assemble(article.getAuthor(), currentProfile))
                .build();
    }

}
